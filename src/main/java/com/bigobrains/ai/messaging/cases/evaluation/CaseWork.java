package com.bigobrains.ai.messaging.cases.evaluation;

import com.bigobrains.ai.messaging.cases.Case;
import com.bigobrains.ai.messaging.cases.evaluation.delegator.CaseEvaluatorExecutor;
import com.bigobrains.ai.messaging.cases.evaluation.delegator.CaseExecutorResult;
import com.bigobrains.ai.messaging.cases.evaluation.delegator.EvaluationAdvise;
import com.bigobrains.ai.messaging.cases.evaluation.flow.FlowExecution;
import com.bigobrains.ai.messaging.cases.evaluation.flow.UnitWork;
import com.bigobrains.ai.messaging.cases.evaluation.flow.WorkResult;
import com.bigobrains.ai.messaging.cases.evaluation.flow.WorkStatus;
import lombok.Getter;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

import java.util.UUID;

public class CaseWork implements UnitWork {

    private final String id;
    private final String name;
    private final Case aCase;
    private final EvaluationAdvise advise;
    private final CaseEvaluatorExecutor caseEvaluatorExecutor;

    @Getter
    private CaseWorkResult workResult;

    public CaseWork(String name, Case aCase, EvaluationAdvise advise, CaseEvaluatorExecutor caseEvaluatorExecutor) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.aCase = aCase;
        this.advise = advise;
        this.caseEvaluatorExecutor = caseEvaluatorExecutor;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Publisher<WorkResult> execute(FlowExecution flowExecution) {
        CaseExecutorResult executorResult = caseEvaluatorExecutor.execute(flowExecution.getVariable(Case.ALIAS) != null ? (Case) flowExecution.getVariable(Case.ALIAS) : aCase, advise);
        Case o = executorResult.getCase();
        flowExecution.setVariable(Case.ALIAS, o);
        WorkStatus workStatus = "ACCEPTED".equals(o.getStatus()) ? WorkStatus.COMPLETED : WorkStatus.FAILED;
        this.workResult = new CaseWorkResult(name, workStatus, executorResult.getExtensions());
        return Mono.just(this.workResult);
    }

    @Override
    public String toString() {
        return "CaseWork{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
