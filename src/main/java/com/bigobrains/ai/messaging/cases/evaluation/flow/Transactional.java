package com.bigobrains.ai.messaging.cases.evaluation.flow;

import org.reactivestreams.Publisher;

public interface Transactional {

    Publisher<WorkResult> compensate(FlowExecution flowExecution);
}
