package com.bigobrains.ai.messaging.cases.function;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.bigobrains.ai.messaging.cases.management.users.User;
import com.bigobrains.ai.messaging.cases.management.users.UserRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;

public class GetSolicitorRoleByEmailIdService implements Function<GetSolicitorRoleByEmailIdService.RequestEntity, GetSolicitorRoleByEmailIdService.ResponseEntity> {

    private final UserRepository userRepository;
    private static final Logger LOG = LoggerFactory.getLogger(GetSolicitorRoleByEmailIdService.class);

    public GetSolicitorRoleByEmailIdService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public ResponseEntity apply(RequestEntity requestEntity) {
        User user = userRepository.findByEmailId(requestEntity.solicitorId);
        ResponseEntity responseEntity = new ResponseEntity(user != null ? user.getRole() : User.Role.ANONYMOUS_USER);
        LOG.info("Solicitor: {}, Role: {}", requestEntity.solicitorId, responseEntity.role);
        return responseEntity;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonClassDescription("solicitorId: Solicitor's EmailId.")
    public static class RequestEntity {
        private String solicitorId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonClassDescription("role: The solicitor's role configured in the system")
    public static class ResponseEntity {
        private User.Role role;
    }
}
