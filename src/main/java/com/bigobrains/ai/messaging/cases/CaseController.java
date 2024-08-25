package com.bigobrains.ai.messaging.cases;

import com.bigobrains.ai.messaging.cases.evaluation.delegator.CaseEvaluatorDelegator;
import com.bigobrains.ai.utils.RandomId;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashSet;
import java.util.List;

import static com.bigobrains.ai.messaging.cases.Case.STANDARD;

@RestController
public class CaseController {

    private final CaseRepository caseRepository;
    private final CaseEvaluatorDelegator caseEvaluatorDelegator;

    public CaseController(CaseRepository caseRepository, CaseEvaluatorDelegator caseEvaluatorDelegator) {
        this.caseRepository = caseRepository;
        this.caseEvaluatorDelegator = caseEvaluatorDelegator;
    }

    @ApiResponse(responseCode = "200", description = "Successful operation",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Case.class),
                    examples = @ExampleObject(
                            name = "Case",
                            value = "[{\"caseId\": \"92534270\", \"solicitorId\": \"barvind88@gmail.com\", \"subject\": \"Fw: Address change request\", \"createTS\": \"2024-08-15T14:41:01.042547100\", \"data\": \"DQpJIHdvdWxkIGxpa2UgdG8gY2hhbmdlIHRoZSBhZGRyZXNzIG9uIHRoZSBmaWxlIHRvIGJlbG93LiBNZXJjaGFudElEOiAgMDAxNDcyOTk5MzM1ODk3NDk3MQ0KDQpOZXcgQWRkcmVzcw0KPT09PT09PT09PT09PT09PQ0KMTUwIFcgTWFpbiBTdCwgQXB0I3MiwgV2F1a2VzaGEuIFdJIC0gNTMxODYuDQoNClRoYW5rcw0KQXJ2aW5kIEINCg==\", \"status\": \"FAILED\", \"assignedTo\": \"Lee Chambers\" }]"
                    )
            )
    )
    @GetMapping(value = "/cases")
    public List<Case> getCases() {
        return caseRepository.findAll();
    }

    @ApiResponse(responseCode = "200", description = "Successful operation",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Case.class),
                    examples = @ExampleObject(
                            name = "Case",
                            value = "{\"caseId\": \"92534270\", \"solicitorId\": \"barvind88@gmail.com\", \"subject\": \"Fw: Address change request\", \"createTS\": \"2024-08-15T14:41:01.042547100\", \"data\": \"DQpJIHdvdWxkIGxpa2UgdG8gY2hhbmdlIHRoZSBhZGRyZXNzIG9uIHRoZSBmaWxlIHRvIGJlbG93LiBNZXJjaGFudElEOiAgMDAxNDcyOTk5MzM1ODk3NDk3MQ0KDQpOZXcgQWRkcmVzcw0KPT09PT09PT09PT09PT09PQ0KMTUwIFcgTWFpbiBTdCwgQXB0I3MiwgV2F1a2VzaGEuIFdJIC0gNTMxODYuDQoNClRoYW5rcw0KQXJ2aW5kIEINCg==\", \"status\": \"FAILED\", \"assignedTo\": \"Lee Chambers\" }"
                    )
            )
    )
    @PostMapping(value = "/cases")
    public Case postCase(@RequestBody Case c) {
        String caseId = RandomId.nextId();
        return caseEvaluatorDelegator.evaluate(caseRepository.save(new Case(
                caseId,
                STANDARD,
                c.getSolicitorId(),
                c.getSubject(),
                c.getCreatedAt(),
                c.getData(),
                c.getStatus(),
                c.getAssignedTo(),
                null,
                new LinkedHashSet<>()
        )));
    }

    @PostMapping(value = "/cases/{caseId}/clearances")
    public Case postClearance(@PathVariable String caseId, @RequestBody Case.Clearance clearance) {
        Case aCase = caseRepository.findById(caseId);
        if (aCase != null) {
            if (aCase.getClearances().remove(clearance)) {
                aCase.getClearances().add(clearance);
            }
            return caseEvaluatorDelegator.evaluate(caseRepository.save(new Case(
                    aCase.getCaseId(),
                    aCase.getCaseType(),
                    aCase.getSolicitorId(),
                    aCase.getSubject(),
                    aCase.getCreatedAt(),
                    aCase.getData(),
                    aCase.getStatus(),
                    aCase.getAssignedTo(),
                    aCase.getWorkFlow(),
                    aCase.getClearances()
            )));
        }
        return null;
    }

    @ApiResponse(responseCode = "200", description = "Successful operation",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Case.class),
                    examples = @ExampleObject(
                            name = "Case",
                            value = "{\"caseId\": \"92534270\", \"solicitorId\": \"barvind88@gmail.com\", \"subject\": \"Fw: Address change request\", \"createTS\": \"2024-08-15T14:41:01.042547100\", \"data\": \"DQpJIHdvdWxkIGxpa2UgdG8gY2hhbmdlIHRoZSBhZGRyZXNzIG9uIHRoZSBmaWxlIHRvIGJlbG93LiBNZXJjaGFudElEOiAgMDAxNDcyOTk5MzM1ODk3NDk3MQ0KDQpOZXcgQWRkcmVzcw0KPT09PT09PT09PT09PT09PQ0KMTUwIFcgTWFpbiBTdCwgQXB0I3MiwgV2F1a2VzaGEuIFdJIC0gNTMxODYuDQoNClRoYW5rcw0KQXJ2aW5kIEINCg==\", \"status\": \"FAILED\", \"assignedTo\": \"Lee Chambers\" }"
                    )
            )
    )
    @PutMapping(value = "/cases/{caseId}/evaluate")
    public Case evaluateCase(@PathVariable(value = "caseId") String caseId, @RequestBody Case c) {
        if (caseId != null && caseRepository.findById(caseId) != null) {
            return caseEvaluatorDelegator.evaluate(c);
        }
        return c;
    }
}
