package com.bigobrains.ai.messaging.cases.function;

import com.bigobrains.ai.messaging.MailService;
import com.fasterxml.jackson.annotation.JsonClassDescription;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.function.Function;

public class SendEmailByEmailIdService implements Function<SendEmailByEmailIdService.RequestEntity, SendEmailByEmailIdService.ResponseEntity> {

    private final MailService mailService;
    private final Session session;

    public SendEmailByEmailIdService(MailService mailService, Session session) {
        this.mailService = mailService;
        this.session = session;
    }

    @Override
    public ResponseEntity apply(RequestEntity requestEntity) {
        try {
            MimeMessage mimeMessage = new MimeMessage(session);
            mimeMessage.setRecipients(Message.RecipientType.TO, InternetAddress.parse(requestEntity.emailId, false));
            mimeMessage.setSubject(requestEntity.subject);
            mimeMessage.setText(requestEntity.body);
            mailService.sendMail(mimeMessage);
        } catch (MessagingException ignored) {}
        return new ResponseEntity("Success");
    }

    @JsonClassDescription(
            """
            emailId: Recipient of the Email
            subject: Email Subject
            body: Email Body
            """)
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RequestEntity {
        private String emailId;
        private String subject;
        private String body;
    }

    @JsonClassDescription("status: The status of SendEmail activity")
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResponseEntity {
        private String status;
    }
}
