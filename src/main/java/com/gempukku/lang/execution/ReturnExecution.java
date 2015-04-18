package com.gempukku.lang.execution;

import com.gempukku.lang.*;

public class ReturnExecution implements Execution {
    private ExecutableStatement _result;

    private boolean _stackedExecution;
    private boolean _returnedResult;

    public ReturnExecution(ExecutableStatement result) {
        _result = result;
    }

    @Override
    public boolean hasNextExecution(ExecutionContext executionContext) {
        if (!_stackedExecution)
            return true;
        if (!_returnedResult)
            return true;
        return false;
    }

    @Override
    public ExecutionProgress executeNextStatement(ExecutionContext executionContext, ExecutionCostConfiguration configuration) throws ExecutionException {
        if (!_stackedExecution) {
            executionContext.stackExecution(_result.createExecution());
            _stackedExecution = true;
            return new ExecutionProgress(configuration.getStackExecution());
        }
        if (!_returnedResult) {
            executionContext.setReturnValue(executionContext.getContextValue());
            _returnedResult = true;
            return new ExecutionProgress(configuration.getGetContextValue() + configuration.getSetReturnValue());
        }
        return null;
    }
}
