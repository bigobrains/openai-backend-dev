package com.bigobrains.ai.messaging.cases.evaluation.delegator;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationAdvise {

    private String userText;
    private String schema;
    private Class<?> clazz;
}
