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

public class MathExecution implements Execution {
    private int line;
    private ExecutableStatement left;
    private Operator operator;
    private ExecutableStatement right;
    private boolean assignToLeft;

    private boolean stackedLeft;
    private boolean resolvedLeft;
    private boolean stackedRight;
    private boolean resolvedAndAssignedSum;

    private Variable leftValue;

    public MathExecution(int line, ExecutableStatement left, Operator operator, ExecutableStatement right, boolean assignToLeft) {
        this.line = line;
        this.left = left;
        this.operator = operator;
        this.right = right;
        this.assignToLeft = assignToLeft;
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
            if (rightValue.getType() == Variable.Type.NUMBER && leftValue.getType() == Variable.Type.NUMBER) {
                final float valueLeft = ((Number) leftValue.getValue()).floatValue();
                final float valueRight = ((Number) rightValue.getValue()).floatValue();
                Object result;
                if (operator == Operator.SUBTRACT || operator == Operator.SUBTRACT_ASSIGN) {
                    result = valueLeft - valueRight;
                } else if (operator == Operator.DIVIDE || operator == Operator.DIVIDE_ASSIGN) {
                    result = valueLeft / valueRight;
                } else if (operator == Operator.MULTIPLY || operator == Operator.MULTIPLY_ASSIGN) {
                    result = valueLeft * valueRight;
                } else if (operator == Operator.MOD || operator == Operator.MOD_ASSIGN) {
                    result = valueLeft % valueRight;
                } else if (operator == Operator.GREATER_OR_EQUAL) {
                    result = valueLeft >= valueRight;
                } else if (operator == Operator.GREATER) {
                    result = valueLeft > valueRight;
                } else if (operator == Operator.LESS_OR_EQUAL) {
                    result = valueLeft <= valueRight;
                } else if (operator == Operator.LESS) {
                    result = valueLeft < valueRight;
                } else {
                    throw new ExecutionException(line, "Unknown operator " + operator);
                }

                if (assignToLeft) {
                    leftValue.setValue(result);
                }
                executionContext.setContextValue(new Variable(result));
            } else {
                throw new ExecutionException(line, "Unable to perform mathematical operation on two non-number values "
                        + leftValue.getType() + " and " + rightValue.getType());
            }
            resolvedAndAssignedSum = true;
            return new ExecutionProgress(configuration.getGetContextValue()
                    + configuration.getOtherMathOperation()
                    + configuration.getSetContextValue());
        }
        return null;
    }

}
