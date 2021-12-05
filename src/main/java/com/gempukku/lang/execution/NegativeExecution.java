// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package com.gempukku.lang.execution;

import com.gempukku.lang.ExecutableStatement;
import com.gempukku.lang.Execution;
import com.gempukku.lang.ExecutionContext;
import com.gempukku.lang.ExecutionCostConfiguration;
import com.gempukku.lang.ExecutionException;
import com.gempukku.lang.ExecutionProgress;
import com.gempukku.lang.Variable;

public class NegativeExecution implements Execution {
    private int line;
    private ExecutableStatement expression;

    private boolean stackedExpression;
    private boolean assignedValue;

    public NegativeExecution(int line, ExecutableStatement expression) {
        this.line = line;
        this.expression = expression;
    }

    @Override
    public boolean hasNextExecution(ExecutionContext executionContext) {
        if (!stackedExpression) {
            return true;
        }
        return !assignedValue;
    }

    @Override
    public ExecutionProgress executeNextStatement(ExecutionContext executionContext, ExecutionCostConfiguration configuration)
            throws ExecutionException {
        if (!stackedExpression) {
            stackedExpression = true;
            executionContext.stackExecution(expression.createExecution());
            return new ExecutionProgress(configuration.getStackExecution());
        }
        if (!assignedValue) {
            assignedValue = true;
            final Variable contextValue = executionContext.getContextValue();
            if (contextValue.getType() != Variable.Type.NUMBER) {
                throw new ExecutionException(line, "Expected NUMBER");
            }
            executionContext.setContextValue(new Variable(-((Number) contextValue.getValue()).floatValue()));
            return new ExecutionProgress(configuration.getGetContextValue() + configuration.getSetContextValue());
        }
        return null;
    }
}
