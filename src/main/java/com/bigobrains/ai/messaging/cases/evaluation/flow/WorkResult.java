package com.bigobrains.ai.messaging.cases.evaluation.flow;

import java.util.Map;
import java.util.Set;

public interface WorkResult {

    String getName();
    WorkStatus getStatus();
    Set<WorkResult> getResults();
    Map<String, Object> getExtensions();
}
