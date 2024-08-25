package com.bigobrains.ai.messaging.cases.evaluation.delegator;

import com.bigobrains.ai.messaging.cases.Case;

public interface CaseEvaluatorExecutor {

    CaseExecutorResult execute(Case o, EvaluationAdvise advise);
}
