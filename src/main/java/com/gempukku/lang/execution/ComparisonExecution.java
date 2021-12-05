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

public class ComparisonExecution implements Execution {
    private ExecutableStatement left;
    private Operator operator;
    private ExecutableStatement right;

    private boolean stackedLeft;
    private boolean resolvedLeft;
    private boolean stackedRight;
    private boolean resolvedAndAssignedSum;

    private Variable leftValue;

    public ComparisonExecution(ExecutableStatement left, Operator operator, ExecutableStatement right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }

    @Override
    public boolean hasNextExecution(ExecutionContext executionContext) {
        if (!stackedLeft) {
            return true;
        }
        if (!resolvedLeft) {
            return true;
        }
        if (!stackedRight) {
            return true;
        }
        return !resolvedAndAssignedSum;
    }

    @Override
    public ExecutionProgress executeNextStatement(ExecutionContext executionContext, ExecutionCostConfiguration configuration)
            throws ExecutionException {
        if (!stackedLeft) {
            executionContext.stackExecution(left.createExecution());
            stackedLeft = true;
            return new ExecutionProgress(configuration.getStackExecution());
        }
        if (!resolvedLeft) {
            leftValue = executionContext.getContextValue();
            resolvedLeft = true;
            return new ExecutionProgress(configuration.getGetContextValue());
        }
        if (!stackedRight) {
            executionContext.stackExecution(right.createExecution());
            stackedRight = true;
            return new ExecutionProgress(configuration.getStackExecution());
        }
        if (!resolvedAndAssignedSum) {
            Variable rightValue = executionContext.getContextValue();
            if (leftValue.getType() != rightValue.getType()) {
                executionContext.setContextValue(new Variable(operator == Operator.NOT_EQUALS));
            } else {
                boolean equals;
                if (leftValue.getType() == Variable.Type.STRING) {
                    equals = leftValue.getValue().equals(rightValue.getValue());
                } else if (leftValue.getType() == Variable.Type.NUMBER) {
                    equals = ((Number) leftValue.getValue()).floatValue() == ((Number) rightValue.getValue()).floatValue();
                } else {
                    equals = leftValue.getValue() == rightValue.getValue();
                }
                if (equals) {
                    executionContext.setContextValue(new Variable(operator == Operator.EQUALS));
                } else {
                    executionContext.setContextValue(new Variable(operator == Operator.NOT_EQUALS));
                }
            }
            resolvedAndAssignedSum = true;
            return new ExecutionProgress(configuration.getGetContextValue()
                    + configuration.getCompareValues()
                    + configuration.getSetContextValue());
        }
        return null;
    }

}
