package com.bigobrains.ai.messaging.cases.evaluation.flow;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;

public class WorkFlowPipeline extends SyncFlow {

    private final WorkFlow workFlow;

    public WorkFlowPipeline(String name, WorkFlow workFlow) {
        super(UUID.randomUUID().toString(), name);
        this.workFlow = workFlow;
    }

    @Override
    public Set<Work> getWorks() {
        return Collections.singleton(workFlow);
    }
}
