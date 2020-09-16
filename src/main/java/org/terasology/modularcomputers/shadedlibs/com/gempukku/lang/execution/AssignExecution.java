// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.execution;

import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.ExecutableStatement;
import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.Execution;
import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.ExecutionContext;
import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.ExecutionCostConfiguration;
import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.ExecutionException;
import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.ExecutionProgress;
import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.Variable;

public class AssignExecution implements Execution {
    private final ExecutableStatement _variable;
    private final ExecutableStatement _value;

    private boolean _stackedVariable;
    private boolean _extractedVariable;
    private boolean _stackedValue;
    private boolean _assignedValue;

    private Variable _variablePointer;

    public AssignExecution(ExecutableStatement variable, ExecutableStatement value) {
        _variable = variable;
        _value = value;
    }

    @Override
    public boolean hasNextExecution(ExecutionContext executionContext) {
        if (!_stackedVariable)
            return true;
        if (!_extractedVariable)
            return true;
        if (!_stackedValue)
            return true;
        return !_assignedValue;
    }

    @Override
    public ExecutionProgress executeNextStatement(ExecutionContext executionContext,
                                                  ExecutionCostConfiguration configuration) throws ExecutionException {
        if (!_stackedVariable) {
            executionContext.stackExecution(_variable.createExecution());
            _stackedVariable = true;
            return new ExecutionProgress(configuration.getStackExecution());
        }
        if (!_extractedVariable) {
            _variablePointer = executionContext.getContextValue();
            _extractedVariable = true;
            return new ExecutionProgress(configuration.getGetContextValue());
        }
        if (!_stackedValue) {
            executionContext.stackExecution(_value.createExecution());
            _stackedValue = true;
            return new ExecutionProgress(configuration.getStackExecution());
        }
        if (!_assignedValue) {
            executionContext.setVariableValue(_variablePointer, executionContext.getContextValue().getValue());
            _assignedValue = true;
            return new ExecutionProgress(configuration.getGetContextValue() + configuration.getSetVariable());
        }
        return null;
    }
}
