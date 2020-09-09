// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package com.gempukku.lang.execution;

import com.gempukku.lang.ExecutableStatement;
import com.gempukku.lang.Execution;
import com.gempukku.lang.ExecutionContext;
import com.gempukku.lang.ExecutionCostConfiguration;
import com.gempukku.lang.ExecutionException;
import com.gempukku.lang.ExecutionProgress;
import com.gempukku.lang.Variable;

public class NegateExecution implements Execution {
    private final int _line;
    private final ExecutableStatement _expression;

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
        return !_assignedValue;
    }

    @Override
    public ExecutionProgress executeNextStatement(ExecutionContext executionContext,
                                                  ExecutionCostConfiguration configuration) throws ExecutionException {
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
