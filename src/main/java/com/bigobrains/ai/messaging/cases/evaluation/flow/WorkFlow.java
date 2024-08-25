package com.bigobrains.ai.messaging.cases.evaluation.flow;

import org.reactivestreams.Publisher;

public interface WorkFlow extends Work {
    WorkFlow schedule(Work... works);
    Publisher<WorkResult> execute(FlowExecution flowExecution);
}
