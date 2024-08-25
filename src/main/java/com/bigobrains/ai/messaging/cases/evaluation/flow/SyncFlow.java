package com.bigobrains.ai.messaging.cases.evaluation.flow;

import lombok.Getter;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public class SyncFlow implements WorkFlow {

    private final String id;
    @Getter
    private final String name;
    @Getter
    private final Set<Work> works;

    public SyncFlow(String id, String name) {
        this.id = id;
        this.name = name;
        this.works = new LinkedHashSet<>();
    }

    @Override
    public WorkFlow schedule(Work... works) {
        this.works.addAll(Arrays.asList(works));
        return this;
    }


    @Override
    public Publisher<WorkResult> execute(FlowExecution flowExecution) {
        AtomicBoolean atomicBoolean = new AtomicBoolean(false);
        SyncExecutor flowExecutor = new SyncExecutor(flowExecution);
        return Flux.from(flowExecutor.execute(new WorkFlowPipeline("*", this))).flatMap(o -> {
            if (o.getStatus().equals(WorkStatus.FAILED) && !atomicBoolean.get()) {
                atomicBoolean.set(true);
                return flowExecutor.compensate(o, flowExecution);
            }
            return Flux.just(o);
        }).take(1);
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return "SyncFlow{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", works=" + works +
                '}';
    }
}
