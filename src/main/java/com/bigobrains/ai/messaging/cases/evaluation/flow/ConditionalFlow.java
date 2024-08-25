package com.bigobrains.ai.messaging.cases.evaluation.flow;

import java.util.UUID;
import java.util.function.Predicate;

public class ConditionalFlow extends SyncFlow {

    private final Predicate<FlowExecution> predicate;

    public ConditionalFlow(String name, Predicate<FlowExecution> predicate) {
        super(UUID.randomUUID().toString(), name);
        this.predicate = predicate;
    }

    Predicate<FlowExecution> getPredicate() {
        return predicate;
    }
}
