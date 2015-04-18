package com.gempukku.lang.execution;

import com.gempukku.lang.*;

public class NegateExecution implements Execution {
    private int _line;
    private ExecutableStatement _expression;

    private boolean _stackedExpression;
    private boolean _assignedValue;

    public NegateExecution(int line, ExecutableStatement expression) {
        _line = line;
        _expression = expression;
    }

    @Override
    public boolean hasNextExecution(ExecutionContext executionContext) {
        if (!_stackedExpression)
            return true;
        if (!_assignedValue)
            return true;
        return false;
    }

    @Override
    public ExecutionProgress executeNextStatement(ExecutionContext executionContext, ExecutionCostConfiguration configuration) throws ExecutionException {
        if (!_stackedExpression) {
            _stackedExpression = true;
            executionContext.stackExecution(_expression.createExecution());
            return new ExecutionProgress(configuration.getStackExecution());
        }
        if (!_assignedValue) {
            _assignedValue = true;
            final Variable contextValue = executionContext.getContextValue();
            if (contextValue.getType() != Variable.Type.BOOLEAN)
                throw new ExecutionException(_line, "Expected BOOLEAN");
            executionContext.setContextValue(new Variable(!(Boolean) contextValue.getValue()));
            return new ExecutionProgress(configuration.getGetContextValue() + configuration.getSetContextValue());
        }
        return null;
    }
}
