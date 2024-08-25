package com.bigobrains.ai.messaging;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EmailController {

    private final MailService mailService;
    private final Session session;

    public EmailController(MailService mailService, Session session) {
        this.mailService = mailService;
        this.session = session;
    }

    @PostMapping(value = "/emails")
    public ResponseEntity<?> sendEmail(@RequestBody Email email) {
        try {
            MimeMessage mimeMessage = new MimeMessage(session);
            mimeMessage.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email.to, false));
            mimeMessage.setSubject(email.subject);
            mimeMessage.setText(email.body);
            mailService.sendMail(mimeMessage);
        } catch (MessagingException ignored) {}

        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class Email {

        private String to;
        private String subject;
        private String body;
    }
}
