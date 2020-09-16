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

public class DefineAndAssignExecution implements Execution {
    private final String _name;
    private final ExecutableStatement _value;

    private boolean _defined;
    private boolean _stackedValue;
    private boolean _assignedValue;

    private Variable _variable;

    public DefineAndAssignExecution(String name, ExecutableStatement value) {
        _name = name;
        _value = value;
    }

    @Override
    public boolean hasNextExecution(ExecutionContext executionContext) {
        if (!_defined)
            return true;
        if (!_stackedValue)
            return true;
        return !_assignedValue;
    }

    @Override
    public ExecutionProgress executeNextStatement(ExecutionContext executionContext,
                                                  ExecutionCostConfiguration configuration) throws ExecutionException {
        if (!_defined) {
            _variable = executionContext.peekCallContext().defineVariable(_name);
            _defined = true;
            return new ExecutionProgress(configuration.getDefineVariable());
        }
        if (!_stackedValue) {
            executionContext.stackExecution(_value.createExecution());
            _stackedValue = true;
            return new ExecutionProgress(configuration.getStackExecution());
        }
        if (!_assignedValue) {
            executionContext.setVariableValue(_variable, executionContext.getContextValue().getValue());
            _assignedValue = true;
            return new ExecutionProgress(configuration.getGetContextValue() + configuration.getSetVariable());
        }
        return null;
    }
}
