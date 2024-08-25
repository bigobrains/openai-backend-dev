package com.bigobrains.ai.messaging.cases.function;

import com.bigobrains.ai.messaging.MailService;
import com.bigobrains.ai.messaging.cases.CaseRepository;
import com.bigobrains.ai.messaging.cases.management.merchants.MerchantRepository;
import com.bigobrains.ai.messaging.cases.management.users.UserRepository;
import jakarta.mail.Session;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;

import java.util.function.Function;

@Configuration
public class FunctionConfigurer {

    private final CaseRepository caseRepository;
    private final UserRepository userRepository;
    private final MerchantRepository merchantRepository;
    private final MailService mailService;
    private final Session session;

    public FunctionConfigurer(CaseRepository caseRepository, UserRepository userRepository, MerchantRepository merchantRepository, MailService mailService, Session session) {
        this.caseRepository = caseRepository;
        this.userRepository = userRepository;
        this.merchantRepository = merchantRepository;
        this.mailService = mailService;
        this.session = session;
    }

    @Bean
    @Description("GetSolicitorRoleByEmailId")
    public Function<GetSolicitorRoleByEmailIdService.RequestEntity, GetSolicitorRoleByEmailIdService.ResponseEntity> getSolicitorRoleByEmailId() {
        return new GetSolicitorRoleByEmailIdService(userRepository);
    }

    @Bean
    @Description("SendEmailByEmailId")
    public Function<SendEmailByEmailIdService.RequestEntity, SendEmailByEmailIdService.ResponseEntity> sendEmailByEmailId() {
        return new SendEmailByEmailIdService(mailService, session);
    }

    @Bean
    @Description("GetMerchantStatusByMerchantId")
    public Function<GetMerchantStatusByMerchantIdService.RequestEntity, GetMerchantStatusByMerchantIdService.ResponseEntity> getMerchantStatusByMerchantId() {
        return new GetMerchantStatusByMerchantIdService(merchantRepository);
    }

    @Bean
    @Description("GetMerchantDetailsByMerchantId")
    public Function<GetMerchantDetailsByMerchantId.RequestEntity, GetMerchantDetailsByMerchantId.ResponseEntity> getMerchantDetailsByMerchantId() {
        return new GetMerchantDetailsByMerchantId(merchantRepository);
    }

    @Bean
    @Description("GetCaseDetailsByCaseId")
    public Function<GetCaseDetailsByCaseIdService.RequestEntity, GetCaseDetailsByCaseIdService.ResponseEntity> getCaseDetailsByCaseId() {
        return new GetCaseDetailsByCaseIdService(caseRepository);
    }
}
