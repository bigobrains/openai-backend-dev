package com.bigobrains.ai.messaging.cases.evaluation;

import com.bigobrains.ai.messaging.cases.evaluation.flow.WorkResult;
import com.bigobrains.ai.messaging.cases.evaluation.flow.WorkStatus;

import java.util.Map;
import java.util.Set;

public class CaseWorkResult implements WorkResult {

    private final String name;
    private final WorkStatus workStatus;
    private final Map<String, Object> extensions;

    public CaseWorkResult(String name, WorkStatus workStatus, Map<String, Object> extensions) {
        this.name = name;
        this.workStatus = workStatus;
        this.extensions = extensions;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public WorkStatus getStatus() {
        return workStatus;
    }

    @Override
    public Set<WorkResult> getResults() {
        return Set.of();
    }

    @Override
    public Map<String, Object> getExtensions() {
        return extensions;
    }
}
