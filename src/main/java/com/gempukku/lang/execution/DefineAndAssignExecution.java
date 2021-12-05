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

public class DefineAndAssignExecution implements Execution {
    private String name;
    private ExecutableStatement value;

    private boolean defined;
    private boolean stackedValue;
    private boolean assignedValue;

    private Variable variable;

    public DefineAndAssignExecution(String name, ExecutableStatement value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public boolean hasNextExecution(ExecutionContext executionContext) {
        if (!defined) {
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
        if (!defined) {
            variable = executionContext.peekCallContext().defineVariable(name);
            defined = true;
            return new ExecutionProgress(configuration.getDefineVariable());
        }
        if (!stackedValue) {
            executionContext.stackExecution(value.createExecution());
            stackedValue = true;
            return new ExecutionProgress(configuration.getStackExecution());
        }
        if (!assignedValue) {
            executionContext.setVariableValue(variable, executionContext.getContextValue().getValue());
            assignedValue = true;
            return new ExecutionProgress(configuration.getGetContextValue() + configuration.getSetVariable());
        }
        return null;
    }
}
