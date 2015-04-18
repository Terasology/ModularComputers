package com.gempukku.lang;

public interface Execution {
    public boolean hasNextExecution(ExecutionContext executionContext);

    public ExecutionProgress executeNextStatement(ExecutionContext executionContext, ExecutionCostConfiguration configuration) throws ExecutionException;
}
