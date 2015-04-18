package com.gempukku.lang.execution;

import com.gempukku.lang.Execution;
import com.gempukku.lang.ExecutionContext;
import com.gempukku.lang.ExecutionCostConfiguration;
import com.gempukku.lang.ExecutionException;
import com.gempukku.lang.ExecutionProgress;

public abstract class SimpleExecution implements Execution {
    private boolean _executed;

    public boolean hasNextExecution(ExecutionContext executionContext) {
        return !_executed;
    }

    public ExecutionProgress executeNextStatement(ExecutionContext executionContext, ExecutionCostConfiguration configuration) throws ExecutionException {
        final ExecutionProgress result = execute(executionContext, configuration);
        _executed = true;
        return result;
    }

    protected abstract ExecutionProgress execute(ExecutionContext context, ExecutionCostConfiguration configuration) throws ExecutionException;
}
