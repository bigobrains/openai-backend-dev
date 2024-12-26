package com.bigobrains.ai.messaging.cases.evaluation.delegator;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.bigobrains.ai.databind.ObjectMapperFactory;
import com.bigobrains.ai.messaging.cases.Case;
import com.bigobrains.ai.messaging.cases.CaseRepository;
import com.bigobrains.ai.messaging.cases.evaluation.CaseWorkExecutorResult;
import com.bigobrains.ai.messaging.cases.management.StaticVectorStore;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.azure.openai.AzureOpenAiChatOptions;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

@Service
public final class DefaultCaseEvaluatorExecutor implements CaseEvaluatorExecutor {

    private final ChatClient simpleChatClient;
    private final CaseRepository caseRepository;
    private final ObjectMapper serializerMapper;

    private static final String ADVISE_SYSTEM = """
            ---------------------------------
            {playbook}
            ---------------------------------
            Given the context and provided playbook, help the organization to accept, and handle system maintenance activities. Maintenance requests are handled using the internal Case Management System. Each request would have an associated caseId, an uniqueId for tracking and resolution, with it. Follow the outlined procedures, and perform all the required validation as per the guidelines. If you need more information, please explicitly state that in the response. If you need approval, from another department to execute a change, please reach out to the contact provided in the playbook, via Email.
            """;

    private static final Logger LOG = LoggerFactory.getLogger(DefaultCaseEvaluatorExecutor.class);

    public DefaultCaseEvaluatorExecutor(VectorStore vectorStore, ChatClient.Builder chatClientBuilder, CaseRepository caseRepository, ObjectMapperFactory mapperFactory) {
        this.simpleChatClient = chatClientBuilder
                .defaultSystem(s -> s.text(ADVISE_SYSTEM).params(Map.of(
                        "playbook", contentList(vectorStore)
                )))
                .defaultAdvisors(new SimpleLoggerAdvisor())
                .build();
        this.caseRepository = caseRepository;
        this.serializerMapper = mapperFactory.serializerMapper();
    }

    public CaseExecutorResult execute(Case o, EvaluationAdvise advise) {

        String text = null;
        BeanOutputConverter<?> beanOutputConverter = new BeanOutputConverter<>(advise.getClazz());
        try {
            ChatResponse response = simpleChatClient.prompt()
                    .user(u -> u.text(advise.getUserText()).params(Map.of(
                            "schema", advise.getSchema(),
                            "case", o.toString()
                    )))
                    .advisors(a -> a
                            .param(CHAT_MEMORY_CONVERSATION_ID_KEY, o.getCaseId())
                            .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10000))
                    .options(AzureOpenAiChatOptions.builder().functions(Set.of("getSolicitorRoleByEmailId", "sendEmailByEmailId", "getMerchantStatusByMerchantId", "getMerchantDetailsByMerchantId")).build())
                    .call().chatResponse();
            text = StringEscapeUtils.unescapeJson(response.getResult().getOutput().getContent());
            LOG.info("{}: {}", advise.getClazz().getSimpleName(), text);
            if (text != null) {
                text = text.contains("```json") ? StringUtils.substringBefore(StringUtils.substringAfter(text, "```json"), "```") : text;
                CaseWorkExecutorResult e = (CaseWorkExecutorResult) beanOutputConverter.convert(text);
                if (e != null) {
                    String caseType = e.getCaseType() != null ? e.getCaseType() : o.getCaseType();
                    Set<Case.Clearance> clearances = o.getClearances() != null ? o.getClearances() : new LinkedHashSet<>();
                    Set<Case.Clearance> sought = e.getClearances() != null ? e.getClearances().stream().filter(clearance -> !clearances.contains(clearance)).collect(Collectors.toSet()) : clearances;
                    if (!sought.isEmpty()) {
                        clearances.addAll(sought);
                    }
                    return new CaseExecutorResult(serializerMapper.convertValue(e, new TypeReference<>() {
                    }), caseRepository.save(new Case(
                            o.getCaseId(),
                            caseType,
                            o.getSolicitorId(),
                            o.getSubject(),
                            o.getCreatedAt(),
                            o.getData(),
                            "STOP".equals(e.getStatus()) ? Case.Status.HOLD.toString() : Case.Status.ACCEPTED.toString(),
                            o.getAssignedTo(),
                            null,
                            clearances
                    )));
                }
            }
        } catch (Exception ex) {
            LOG.error("{}", ExceptionUtils.getStackTrace(ex));
        }

        return new CaseExecutorResult(null, caseRepository.save(new Case(
                o.getCaseId(),
                o.getCaseType(),
                o.getSolicitorId(),
                o.getSubject(),
                o.getCreatedAt(),
                o.getData(),
                Case.Status.FAILED.toString(),
                o.getAssignedTo(),
                null,
                o.getClearances())
        ));
    }

    private static String contentList(VectorStore vectorStore) {
        List<Document> documents = ((StaticVectorStore) vectorStore).similaritySearchAll();
        return documents.stream().map(Document::getFormattedContent).collect(Collectors.joining(System.lineSeparator()));
    }
}
