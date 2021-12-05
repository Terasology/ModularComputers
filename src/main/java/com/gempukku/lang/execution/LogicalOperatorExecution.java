// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package com.gempukku.lang.execution;

import com.gempukku.lang.ExecutableStatement;
import com.gempukku.lang.Execution;
import com.gempukku.lang.ExecutionContext;
import com.gempukku.lang.ExecutionCostConfiguration;
import com.gempukku.lang.ExecutionException;
import com.gempukku.lang.ExecutionProgress;
import com.gempukku.lang.Operator;
import com.gempukku.lang.Variable;

public class LogicalOperatorExecution implements Execution {
    private int line;
    private ExecutableStatement left;
    private Operator operator;
    private ExecutableStatement right;

    private boolean terminated;

    private boolean stackedLeft;
    private boolean resolvedLeft;

    private boolean stackedRight;
    private boolean resolvedRight;

    public LogicalOperatorExecution(int line, ExecutableStatement left, Operator operator, ExecutableStatement right) {
        this.line = line;
        this.left = left;
        this.operator = operator;
        this.right = right;
    }

    @Override
    public boolean hasNextExecution(ExecutionContext executionContext) {
        return !terminated;
    }

    @Override
    public ExecutionProgress executeNextStatement(ExecutionContext executionContext, ExecutionCostConfiguration configuration)
            throws ExecutionException {
        if (!stackedLeft) {
            stackedLeft = true;
            executionContext.stackExecution(left.createExecution());
            return new ExecutionProgress(configuration.getStackExecution());
        }
        if (!resolvedLeft) {
            resolvedLeft = true;
            final Variable contextValue = executionContext.getContextValue();
            if (contextValue.getType() != Variable.Type.BOOLEAN) {
                throw new ExecutionException(line, "Expected BOOLEAN");
            }
            boolean result = (Boolean) contextValue.getValue();
            if (operator == Operator.AND && !result) {
                terminated = true;
                executionContext.setContextValue(new Variable(false));
            } else if (operator == Operator.OR && result) {
                terminated = true;
                executionContext.setContextValue(new Variable(true));
            }
            if (terminated) {
                return new ExecutionProgress(configuration.getGetContextValue() + configuration.getSetContextValue());
            } else {
                return new ExecutionProgress(configuration.getGetContextValue());
            }
        }
        if (!stackedRight) {
            stackedRight = true;
            executionContext.stackExecution(right.createExecution());
            return new ExecutionProgress(configuration.getStackExecution());
        }
        if (!resolvedRight) {
            resolvedRight = true;
            final Variable contextValue = executionContext.getContextValue();
            if (contextValue.getType() != Variable.Type.BOOLEAN) {
                throw new ExecutionException(line, "Expected BOOLEAN");
            }
            terminated = true;
            boolean result = (Boolean) contextValue.getValue();
            executionContext.setContextValue(new Variable(result));
            return new ExecutionProgress(configuration.getGetContextValue() + configuration.getSetContextValue());
        }
        return null;
    }
}
