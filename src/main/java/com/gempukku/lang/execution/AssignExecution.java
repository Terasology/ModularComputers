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

public class AssignExecution implements Execution {
    private ExecutableStatement variable;
    private ExecutableStatement value;

    private boolean stackedVariable;
    private boolean extractedVariable;
    private boolean stackedValue;
    private boolean assignedValue;

    private Variable variablePointer;

    public AssignExecution(ExecutableStatement variable, ExecutableStatement value) {
        this.variable = variable;
        this.value = value;
    }

    @Override
    public boolean hasNextExecution(ExecutionContext executionContext) {
        if (!stackedVariable) {
            return true;
        }
        if (!extractedVariable) {
            return true;
        }
        if (!stackedValue) {
            return true;
        }
        return !assignedValue;
    }

    @Override
    public ExecutionProgress executeNextStatement(ExecutionContext executionContext, ExecutionCostConfiguration configuration)
            throws ExecutionException {
        if (!stackedVariable) {
            executionContext.stackExecution(variable.createExecution());
            stackedVariable = true;
            return new ExecutionProgress(configuration.getStackExecution());
        }
        if (!extractedVariable) {
            variablePointer = executionContext.getContextValue();
            extractedVariable = true;
            return new ExecutionProgress(configuration.getGetContextValue());
        }
        if (!stackedValue) {
            executionContext.stackExecution(value.createExecution());
            stackedValue = true;
            return new ExecutionProgress(configuration.getStackExecution());
        }
        if (!assignedValue) {
            executionContext.setVariableValue(variablePointer, executionContext.getContextValue().getValue());
            assignedValue = true;
            return new ExecutionProgress(configuration.getGetContextValue() + configuration.getSetVariable());
        }
        return null;
    }
}
