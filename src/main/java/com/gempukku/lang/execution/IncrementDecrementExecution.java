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

public class IncrementDecrementExecution implements Execution {
    private int line;
    private ExecutableStatement expression;
    private boolean increment;
    private boolean pre;

    private boolean stackedExecution;
    private boolean finished;

    public IncrementDecrementExecution(int line, ExecutableStatement expression, boolean increment, boolean pre) {
        this.line = line;
        this.expression = expression;
        this.increment = increment;
        this.pre = pre;
    }

    @Override
    public boolean hasNextExecution(ExecutionContext executionContext) {
        return !finished;
    }

    @Override
    public ExecutionProgress executeNextStatement(ExecutionContext executionContext, ExecutionCostConfiguration configuration)
            throws ExecutionException {
        if (!stackedExecution) {
            executionContext.stackExecution(expression.createExecution());
            stackedExecution = true;
            return new ExecutionProgress(configuration.getStackExecution());
        }
        final Variable contextValue = executionContext.getContextValue();
        if (contextValue.getType() != Variable.Type.NUMBER) {
            throw new ExecutionException(line, "Expected NUMBER");
        }

        final float original = ((Number) contextValue.getValue()).floatValue();
        float result = original;
        if (pre) {
            if (increment) {
                result += 1;
            } else {
                result -= 1;
            }
        }
        if (increment) {
            contextValue.setValue(original + 1);
        } else {
            contextValue.setValue(original - 1);
        }
        executionContext.setContextValue(new Variable(result));
        finished = true;

        return new ExecutionProgress(configuration.getGetContextValue() + configuration.getSetContextValue());
    }
}
