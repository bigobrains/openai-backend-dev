package com.bigobrains.ai.messaging.cases.function;

import com.bigobrains.ai.messaging.cases.Case;
import com.bigobrains.ai.messaging.cases.CaseRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.function.Function;

public class GetCaseDetailsByCaseIdService implements Function<GetCaseDetailsByCaseIdService.RequestEntity, GetCaseDetailsByCaseIdService.ResponseEntity> {

    private final CaseRepository caseRepository;

    public GetCaseDetailsByCaseIdService(CaseRepository caseRepository) {
        this.caseRepository = caseRepository;
    }

    @Override
    public ResponseEntity apply(RequestEntity requestEntity) {
        return new ResponseEntity(caseRepository.findById(requestEntity.caseId));
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RequestEntity {
        private String caseId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResponseEntity {
        private Case aCase;
    }
}
