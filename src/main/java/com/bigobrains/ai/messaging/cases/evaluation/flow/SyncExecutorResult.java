package com.bigobrains.ai.messaging.cases.evaluation.flow;


import java.util.Map;
import java.util.Set;

public class SyncExecutorResult implements WorkResult {

    private final String name;
    private final WorkStatus workStatus;
    private final Set<WorkResult> workResults;
    private final Map<String, Object> extensions;

    public SyncExecutorResult(String name, WorkStatus workStatus, Set<WorkResult> workResults, Map<String, Object> extensions) {
        this.name = name;
        this.workStatus = workStatus;
        this.workResults = workResults;
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
        return workResults;
    }

    @Override
    public Map<String, Object> getExtensions() {
        return extensions;
    }
}
