package com.bigobrains.ai.messaging.cases.evaluation.flow;

import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.function.Predicate;

@Service
public class WorkFlowManager {

    public WorkFlow sequentialFlow(String name) {
        return new SyncFlow(UUID.randomUUID().toString(), name);
    }

    public WorkFlow conditionalFlow(String name, Predicate<FlowExecution> predicate) {
        return new ConditionalFlow(name, predicate);
    }
}
