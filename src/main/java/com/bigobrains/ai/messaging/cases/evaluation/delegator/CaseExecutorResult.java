package com.bigobrains.ai.messaging.cases.evaluation.delegator;

import com.bigobrains.ai.messaging.cases.Case;
import lombok.Getter;

import java.util.Map;

public class CaseExecutorResult {

    @Getter
    private final Map<String, Object> extensions;
    private final Case o;

    public CaseExecutorResult(Map<String, Object> extensions, Case o) {
        this.extensions = extensions;
        this.o = o;
    }

    public Case getCase() {
        return o;
    }
}
