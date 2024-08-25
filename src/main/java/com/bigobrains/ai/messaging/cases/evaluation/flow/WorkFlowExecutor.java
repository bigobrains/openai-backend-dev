package com.bigobrains.ai.messaging.cases.evaluation.flow;

import org.reactivestreams.Publisher;

public interface WorkFlowExecutor {
    Publisher<WorkResult> execute(WorkFlow workFlow);
}
