package com.bigobrains.ai.messaging.cases.evaluation.flow;

import java.util.Map;
import java.util.Set;

public class UnitWorkResult implements WorkResult {

    private final String name;
    private final WorkStatus workStatus;

    public UnitWorkResult(String name, WorkStatus workStatus) {
        this.name = name;
        this.workStatus = workStatus;
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
        return null;
    }

    @Override
    public Map<String, Object> getExtensions() {
        return Map.of();
    }
}
