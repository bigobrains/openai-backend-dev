package com.bigobrains.ai.messaging.cases.evaluation.delegator;

import com.bigobrains.ai.databind.ObjectMapperFactory;
import com.bigobrains.ai.messaging.cases.Case;
import com.bigobrains.ai.messaging.cases.CaseRepository;
import com.bigobrains.ai.messaging.cases.evaluation.CaseWork;
import com.bigobrains.ai.messaging.cases.evaluation.CaseWorkExecutorResult;
import com.bigobrains.ai.databind.Organization;
import com.bigobrains.ai.messaging.cases.evaluation.flow.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.expression.MapAccessor;
import org.springframework.core.io.ResourceLoader;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CaseEvaluatorDelegator {

    private final DefaultCaseEvaluatorExecutor evaluatorExecutor;
    private final Organization organization;
    private final WorkFlowManager workFlowManager;
    private final CaseRepository caseRepository;

    private static final ExpressionParser PARSER = new SpelExpressionParser();

    public CaseEvaluatorDelegator(DefaultCaseEvaluatorExecutor evaluatorExecutor, ResourceLoader resourceLoader, WorkFlowManager workFlowManager, ObjectMapperFactory mapperFactory, CaseRepository caseRepository) throws IOException {
        this.evaluatorExecutor = evaluatorExecutor;
        this.workFlowManager = workFlowManager;
        this.caseRepository = caseRepository;
        ObjectMapper serializerMapper = mapperFactory.deSerializerMapper();
        this.organization = serializerMapper.readValue(resourceLoader.getResource("classpath:playbook.json").getInputStream(), new TypeReference<List<Organization>>() {}).get(0);
    }

    public Case evaluate(Case o) {

        List<Organization.Department.Service.Work> works = organization.getDepartments().get(2).getServices().get(0).getWorks();
        FlowExecution flowExecution = FlowExecution.with(FlowExecution.Variables.of(new LinkedHashMap<>()));
        StandardEvaluationContext standardEvaluationContext = new StandardEvaluationContext(flowExecution.getVariables());
        standardEvaluationContext.addPropertyAccessor(new MapAccessor());
        WorkFlow workFlow = flow(works.stream().collect(Collectors.groupingBy(Organization.Department.Service.Work::getWorkFlowId)), o, standardEvaluationContext);
        return Mono.from(workFlow.execute(flowExecution)).map(result -> {
            Case aCase = (Case) flowExecution.getVariable(Case.ALIAS);
            return caseRepository.save(new Case(
                    aCase.getCaseId(),
                    aCase.getCaseType(),
                    aCase.getSolicitorId(),
                    aCase.getSubject(),
                    aCase.getCreatedAt(),
                    aCase.getData(),
                    WorkStatus.COMPLETED.equals(result.getStatus()) ? Case.Status.ACCEPTED.toString() : Case.Status.HOLD.toString(),
                    aCase.getAssignedTo(),
                    workFlow,
                    aCase.getClearances()
            ));
        }).block();
    }

    private WorkFlow flow(Map<String, List<Organization.Department.Service.Work>> works, Case aCase, StandardEvaluationContext standardEvaluationContext) {

        WorkFlow workFlow = null;
        for (Map.Entry<String, List<Organization.Department.Service.Work>> entry : works.entrySet()) {
            String conditional = works.get(entry.getKey()).get(0).getConditional();
            List<Work> workList = entry.getValue().stream().map(o -> new CaseWork(
                    o.getWorkId(),
                    aCase,
                    new EvaluationAdvise(o.getUserText(), o.getSchema(), CaseWorkExecutorResult.class),
                    evaluatorExecutor
            )).collect(Collectors.toList());
            WorkFlow newFlow = conditional != null ? workFlowManager.conditionalFlow(entry.getKey(), o -> Boolean.TRUE.equals(PARSER.parseExpression(conditional).getValue(standardEvaluationContext, Boolean.class))) : workFlowManager.sequentialFlow(entry.getKey());
            workFlow = workFlow != null ? workFlow.schedule(newFlow.schedule(workList.toArray(new Work[]{}))) : newFlow.schedule(workList.toArray(new Work[]{}));
        }
        return workFlow;
    }
}