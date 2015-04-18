package com.gempukku.lang.execution;

import com.gempukku.lang.ExecutableStatement;
import com.gempukku.lang.Execution;
import com.gempukku.lang.ExecutionContext;
import com.gempukku.lang.ExecutionCostConfiguration;
import com.gempukku.lang.ExecutionException;
import com.gempukku.lang.ExecutionProgress;
import com.gempukku.lang.Variable;

public class WhileExecution implements Execution {
    private int _line;
    private ExecutableStatement _condition;
    private ExecutableStatement _statement;

    private boolean _terminated;

    private boolean _conditionStacked;

    public WhileExecution(int line, ExecutableStatement condition, ExecutableStatement statement) {
        _line = line;
        _condition = condition;
        _statement = statement;
    }

    @Override
    public boolean hasNextExecution(ExecutionContext executionContext) {
        if (_terminated)
            return false;

        return true;
    }

    @Override
    public ExecutionProgress executeNextStatement(ExecutionContext executionContext, ExecutionCostConfiguration configuration) throws ExecutionException {
        if (!_conditionStacked) {
            _conditionStacked = true;
            executionContext.stackExecution(_condition.createExecution());
            return new ExecutionProgress(configuration.getStackExecution());
        }
        final Variable value = executionContext.getContextValue();
        if (value.getType() != Variable.Type.BOOLEAN)
            throw new ExecutionException(_line, "Condition not of type BOOLEAN");
        final Boolean result = (Boolean) value.getValue();
        if (!result)
            _terminated = true;
        else {
            executionContext.stackExecution(_statement.createExecution());
            _conditionStacked = false;
        }
        if (_terminated)
            return new ExecutionProgress(configuration.getCompareValues() + configuration.getStackExecution());
        else
            return new ExecutionProgress(configuration.getStackExecution());
    }
}
