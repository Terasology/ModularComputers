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

public class AddExecution implements Execution {
    private int line;
    private ExecutableStatement left;
    private ExecutableStatement right;
    private boolean assignToLeft;

    private boolean stackedLeft;
    private boolean resolvedLeft;
    private boolean stackedRight;
    private boolean resolvedAndAssignedSum;

    private Variable leftValue;

    public AddExecution(int line, ExecutableStatement left, ExecutableStatement right, boolean assignToLeft) {
        this.line = line;
        this.left = left;
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
            Object result;
            if (leftValue.getType() == Variable.Type.STRING) {
                result = convertToString(leftValue) + convertToString(rightValue);
            } else if (rightValue.getType() == Variable.Type.NUMBER && leftValue.getType() == Variable.Type.NUMBER) {
                result = ((Number) leftValue.getValue()).floatValue() + ((Number) rightValue.getValue()).floatValue();
            } else {
                throw new ExecutionException(line, "Unable to add two values of types "
                        + leftValue.getType() + " and " + rightValue.getType());
            }
            if (assignToLeft) {
                leftValue.setValue(result);
            }
            executionContext.setContextValue(new Variable(result));
            resolvedAndAssignedSum = true;
            return new ExecutionProgress(configuration.getGetContextValue() + configuration.getSumValues());
        }
        return null;
    }

    private String convertToString(Variable variable) {
        if (variable.getType() == Variable.Type.STRING) {
            return (String) variable.getValue();
        } else if (variable.getType() == Variable.Type.NUMBER) {
            return String.valueOf(((Number) variable.getValue()).floatValue());
        } else if (variable.getType() == Variable.Type.NULL) {
            return "null";
        } else if (variable.getType() == Variable.Type.BOOLEAN) {
            return ((Boolean) variable.getValue()) ? "true" : "false";
        } else {
            return "";
        }
    }
}
