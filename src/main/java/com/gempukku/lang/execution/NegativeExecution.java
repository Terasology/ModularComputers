package com.gempukku.lang.execution;

import com.gempukku.lang.ExecutableStatement;
import com.gempukku.lang.Execution;
import com.gempukku.lang.ExecutionContext;
import com.gempukku.lang.ExecutionCostConfiguration;
import com.gempukku.lang.ExecutionException;
import com.gempukku.lang.ExecutionProgress;
import com.gempukku.lang.Variable;

public class NegativeExecution implements Execution {
    private int _line;
    private ExecutableStatement _expression;

    private boolean _stackedExpression;
    private boolean _assignedValue;

    public NegativeExecution(int line, ExecutableStatement expression) {
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
            if (contextValue.getType() != Variable.Type.NUMBER)
                throw new ExecutionException(_line, "Expected NUMBER");
            executionContext.setContextValue(new Variable(-((Number) contextValue.getValue()).floatValue()));
            return new ExecutionProgress(configuration.getGetContextValue() + configuration.getSetContextValue());
        }
        return null;
    }
}
