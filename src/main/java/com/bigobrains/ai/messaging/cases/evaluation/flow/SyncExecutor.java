package com.bigobrains.ai.messaging.cases.evaluation.flow;

import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class SyncExecutor implements WorkFlowExecutor {

    private final FlowExecution flowExecution;
    private final AtomicReference<FlowStatus> flowStatus;
    private final Map<String, Transactional> transactions;

    public SyncExecutor(FlowExecution flowExecution) {
        this.flowExecution = flowExecution;
        this.flowStatus = new AtomicReference<>(FlowStatus.CLOSED);
        this.transactions = new LinkedHashMap<>();
    }

    @Override
    public Publisher<WorkResult> execute(WorkFlow workFlow) {

        AtomicReference<WorkStatus> workStatus = new AtomicReference<>(WorkStatus.COMPLETED);
        Set<Work> works = ((SyncFlow) workFlow).getWorks();
        return Flux.fromIterable(works).flatMap(work -> {
            if (work instanceof ConditionalFlow flow) {
                if (flowStatus.get().equals(FlowStatus.OPEN)) {
                    return Flux.empty();
                }
                Predicate<FlowExecution> predicate = ((ConditionalFlow) work).getPredicate();
                if (!predicate.test(flowExecution)) {
                    return Flux.just(new SyncExecutorResult(workFlow.getName(), WorkStatus.SKIPPED, null, null));
                }

                Publisher<WorkResult> flowResult = execute(flow);
                return Mono.from(Flux.from(flowResult).doOnNext(o -> {
                    if (WorkStatus.FAILED.equals(o.getStatus())) {
                        flowStatus.set(FlowStatus.OPEN);
                        workStatus.set(o.getStatus());
                    }
                }).collect(Collectors.toSet()).map(o -> (WorkResult) new SyncExecutorResult(flow.getName(), workStatus.get(), o, null)));
            } else if (work instanceof WorkFlow flow) {
                if (flowStatus.get().equals(FlowStatus.OPEN)) {
                    return Flux.empty();
                }

                Publisher<WorkResult> flowResult = execute(flow);
                return Mono.from(Flux.from(flowResult).doOnNext(o -> {
                    if (WorkStatus.FAILED.equals(o.getStatus())) {
                        flowStatus.set(FlowStatus.OPEN);
                        workStatus.set(o.getStatus());
                    }
                }).collect(Collectors.toSet()).map(o -> (WorkResult) new SyncExecutorResult(flow.getName(), workStatus.get(), o, null)));
            } else {
                Publisher<WorkResult> flowResult = flowStatus.get().equals(FlowStatus.CLOSED) ? work.execute(flowExecution) : Mono.empty();
                return Flux.from(flowResult).filter(o -> {
                    if (flowStatus.get().equals(FlowStatus.CLOSED)) {
                        if (work instanceof Transactional) {
                            transactions.put(work.getName(), (Transactional) work);
                        }

                        if (WorkStatus.FAILED.equals(o.getStatus())) {
                            flowStatus.set(FlowStatus.OPEN);
                        }
                        return true;
                    }
                    return false;
                }).map(o -> (WorkResult) new SyncExecutorResult(o.getName(), o.getStatus(), new LinkedHashSet<>(), o.getExtensions()));
            }
        });
    }

    public Publisher<WorkResult> compensate(WorkResult flowResult, FlowExecution flowExecution) {
        return Flux.just(flowResult).flatMap(result -> {
            if (result.getResults() != null && !result.getResults().isEmpty()) {
                return Mono.from(Flux.fromIterable(result.getResults()).flatMap(o -> compensate(o, flowExecution))
                        .collect(Collectors.toSet()).map(o -> new SyncExecutorResult(flowResult.getName(), flowResult.getStatus(), o, null)));
            }
            Transactional transactional;
            return (transactional = transactions.get(flowResult.getName())) != null ? transactional.compensate(flowExecution) : Flux.just(flowResult);
        });
    }
}
