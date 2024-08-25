package com.bigobrains.ai.messaging.cases.evaluation;

import com.bigobrains.ai.messaging.cases.Case;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CaseWorkExecutorResult {

    private String caseId;
    private String caseType;
    private String status;
    private String departmentName;
    private List<String> validations;
    private List<Case.Clearance> clearances;
}
