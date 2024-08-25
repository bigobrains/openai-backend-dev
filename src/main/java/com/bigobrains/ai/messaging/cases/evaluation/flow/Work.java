package com.bigobrains.ai.messaging.cases.evaluation.flow;

import org.reactivestreams.Publisher;

public interface Work {

    String getId();
    String getName();
    Publisher<WorkResult> execute(FlowExecution flowExecution);
}
