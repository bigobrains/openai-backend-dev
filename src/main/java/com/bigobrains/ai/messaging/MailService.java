package com.bigobrains.ai.messaging;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import jakarta.mail.MessagingException;
import jakarta.mail.Transport;
import jakarta.mail.internet.MimeMessage;

import com.bigobrains.ai.messaging.cases.Case;
import com.bigobrains.ai.messaging.cases.CaseRepository;
import com.bigobrains.ai.messaging.cases.evaluation.CaseWorkExecutorResult;
import com.bigobrains.ai.messaging.cases.evaluation.delegator.CaseEvaluatorDelegator;
import com.bigobrains.ai.messaging.cases.management.agents.AgentManager;
import com.bigobrains.ai.utils.RandomId;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.mail2.jakarta.util.MimeMessageParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    private final ChatClient simpleChatClient;
    private final AgentManager agentManager;
    private final CaseEvaluatorDelegator caseEvaluatorDelegator;
    private final CaseRepository caseRepository;

    private static final String SCHEMA_CASE_ID_SEARCH = """
            {
                "$schema": "http://json-schema.org/draft-07/schema#",
                "type": "object",
                "properties": {
                    "caseId": {
                        "type": "string",
                        "description": "CaseId associated to a system maintenance request."
                    }
                },
                "required": ["caseId"]
            }
            """;

    private static final String ADVISE_CASE_ID_SEARCH = """
            Given a text, as a stateless chat assistant, please extract and return the caseId in a JSON format, strictly adhering to the following schema structure. Do not include any explanations.
            Schema: {schema}
            EmailBody: {body}
            EmailSubject: {subject}
            """;

    private static final String SCHEMA_CASE_CLEARANCE_APPROVE_REJECT = """
            {
                 "$schema": "http://json-schema.org/draft-07/schema#",
                 "type": "object",
                 "properties": {
                     "departmentName": {
                         "type": "string",
                         "enum": [
                             "COLLECTIONS",
                             "LEGAL"
                         ],
                         "description": "Indicates, the department name, the approval is sought from."
                     },
                     "status": {
                         "type": "string",
                         "enum": [
                             "APPROVED",
                             "REJECTED",
                             "NONE"
                         ],
                         "description": "Indicates, if the case was APPROVED or REJECTED."
                     }
                 },
                 "required": ["departmentName", "status"]
             }
            """;

    private static final String ADVISE_CASE_CLEARANCE_APPROVE_REJECT = """
            Given a case for system maintenance request, please review the email provided, and confirm the department name and approval status for the case, in a JSON format, strictly adhering to the following schema structure. Do not include any explanations. Return NOT_SURE, if not sure.
            Schema: {schema}
            EmailBody: {body}
            EmailSubject: {subject}
            """;

    public MailService(ChatClient simpleChatClient, AgentManager agentManager, CaseRepository caseRepository, CaseEvaluatorDelegator caseEvaluatorDelegator) {
        this.simpleChatClient = simpleChatClient;
        this.agentManager = agentManager;
        this.caseEvaluatorDelegator = caseEvaluatorDelegator;
        this.caseRepository = caseRepository;
    }

    private static final Logger LOG = LoggerFactory.getLogger(MailService.class);

    public void handleMail(MimeMessage message) {

        try {
            BeanOutputConverter<CaseWorkExecutorResult> beanOutputConverter = new BeanOutputConverter<>(CaseWorkExecutorResult.class);
            MimeMessageParser mimeMessageParser = new MimeMessageParser(message).parse();
            ChatResponse response = simpleChatClient.prompt()
                    .user(s -> {
                        try {
                            s.text(ADVISE_CASE_ID_SEARCH).params(Map.of(
                                    "schema", SCHEMA_CASE_ID_SEARCH,
                                    "body", mimeMessageParser.getPlainContent(),
                                    "subject", mimeMessageParser.getSubject()
                            ));
                        } catch (MessagingException ignored) {}
                    })
                    .call().chatResponse();
            CaseWorkExecutorResult managedCase = beanOutputConverter.convert(response.getResult().getOutput().getText());
            byte[] bytes = mimeMessageParser.getPlainContent().trim().getBytes();
            if (managedCase != null && managedCase.getCaseId() != null) {
                Case o = caseRepository.findById(managedCase.getCaseId());
                if (o != null) {
                    o = new Case(
                            o.getCaseId(),
                            o.getCaseType(),
                            o.getSolicitorId(),
                            mimeMessageParser.getSubject(),
                            o.getCreatedAt(),
                            Base64.encodeBase64String(bytes),
                            Case.Status.ACKNOWLEDGED.toString(),
                            o.getAssignedTo(),
                            null,
                            o.getClearances()
                    );
                    LOG.info("Received Email communication for a system change. Subject: {}, CaseId: {}", message.getSubject(), o.getCaseId());
                    ChatResponse caseStatus = simpleChatClient.prompt()
                            .user(s -> {
                                try {
                                    s.text(ADVISE_CASE_CLEARANCE_APPROVE_REJECT).params(Map.of(
                                            "schema", SCHEMA_CASE_CLEARANCE_APPROVE_REJECT,
                                            "body", mimeMessageParser.getPlainContent(),
                                            "subject", mimeMessageParser.getSubject()
                                    ));
                                } catch (MessagingException ignored) {}
                            })
                            .call().chatResponse();
                    if (caseStatus != null) {
                        try {
                            BeanOutputConverter<CaseWorkExecutorResult> outputConverter = new BeanOutputConverter<>(CaseWorkExecutorResult.class);
                            CaseWorkExecutorResult executorResult = outputConverter.convert(caseStatus.getResult().getOutput().getText());
                            if (executorResult != null && !"NONE".equals(executorResult.getStatus())) {
                                Set<Case.Clearance> clearances = o.getClearances();
                                String departmentName = executorResult.getDepartmentName();
                                String status = executorResult.getStatus();
                                Case.Clearance clearance = new Case.Clearance(departmentName, status);
                                if (clearances != null && clearances.remove(clearance)) {
                                    o.getClearances().add(clearance);
                                }
                                caseRepository.save(caseEvaluatorDelegator.evaluate(o));
                                return;
                            }
                        } catch (Exception ex) {
                            LOG.error("{}", ExceptionUtils.getStackTrace(ex));
                        }
                    }
                    caseRepository.save(o);
                    caseRepository.save(caseEvaluatorDelegator.evaluate(o));
                    return;
                }
            }
            Case o = new Case(
                    RandomId.nextId(),
                    Case.STANDARD,
                    mimeMessageParser.getFrom(),
                    mimeMessageParser.getSubject(),
                    LocalDateTime.now().toString(),
                    Base64.encodeBase64String(bytes),
                    Case.Status.ACKNOWLEDGED.toString(),
                    agentManager.findNext().getName(),
                    null,
                    new LinkedHashSet<>()
            );
            LOG.info("Received Email communication for a system change. Subject: {}, CaseId: {}", message.getSubject(), o.getCaseId());
            caseRepository.save(o);
            caseRepository.save(caseEvaluatorDelegator.evaluate(o));
        } catch (IOException | MessagingException ignored) {
        }
    }

    public void sendMail(MimeMessage message) {
        try {
            Transport.send(message);
        } catch (MessagingException ex) {
            LOG.error("SendEmailError: {}", ExceptionUtils.getStackTrace(ex));
        }
    }
}
