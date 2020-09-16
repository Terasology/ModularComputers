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

import java.util.List;
import java.util.Map;

public class MapAccessExecution implements Execution {
    private final int _line;
    private final ExecutableStatement _mapStatement;
    private final ExecutableStatement _propertyStatement;

    private boolean _stackedMapStatement;
    private boolean _resolvedMapStatement;
    private boolean _stackedPropertyStatement;
    private boolean _assignedValue;

    private Variable _mapVariable;

    public MapAccessExecution(int line, ExecutableStatement mapStatement, ExecutableStatement propertyStatement) {
        _line = line;
        _mapStatement = mapStatement;
        _propertyStatement = propertyStatement;
    }

    @Override
    public boolean hasNextExecution(ExecutionContext executionContext) {
        if (!_stackedMapStatement)
            return true;
        if (!_resolvedMapStatement)
            return true;
        if (!_stackedPropertyStatement)
            return true;
        return !_assignedValue;
    }

    @Override
    public ExecutionProgress executeNextStatement(ExecutionContext executionContext,
                                                  ExecutionCostConfiguration configuration) throws ExecutionException {
        if (!_stackedMapStatement) {
            _stackedMapStatement = true;
            executionContext.stackExecution(_mapStatement.createExecution());
            return new ExecutionProgress(configuration.getStackExecution());
        }
        if (!_resolvedMapStatement) {
            _resolvedMapStatement = true;
            _mapVariable = executionContext.getContextValue();
            if (_mapVariable.getType() != Variable.Type.MAP && _mapVariable.getType() != Variable.Type.LIST)
                throw new ExecutionException(_line, "Map or list expected");
            return new ExecutionProgress(configuration.getGetContextValue());
        }
        if (!_stackedPropertyStatement) {
            _stackedPropertyStatement = true;
            executionContext.stackExecution(_propertyStatement.createExecution());
            return new ExecutionProgress(configuration.getStackExecution());
        }
        if (!_assignedValue) {
            _assignedValue = true;
            final Variable value = executionContext.getContextValue();
            if (_mapVariable.getType() == Variable.Type.MAP) {
                if (value.getType() != Variable.Type.STRING)
                    throw new ExecutionException(_line, "Property name expected");
                Map<String, Variable> properties = (Map<String, Variable>) _mapVariable.getValue();
                final String propertyName = (String) value.getValue();
                if (!properties.containsKey(propertyName))
                    properties.put(propertyName, new Variable(null));
                executionContext.setContextValue(properties.get(propertyName));
                return new ExecutionProgress(configuration.getGetContextValue() + configuration.getSetContextValue());
            } else {
                if (value.getType() != Variable.Type.NUMBER)
                    throw new ExecutionException(_line, "List index expected");
                List<Variable> values = (List<Variable>) _mapVariable.getValue();
                int index = ((Number) value.getValue()).intValue();
                if (index < 0 || index >= values.size())
                    throw new ExecutionException(_line, "List index out of bounds");
                executionContext.setContextValue(values.get(index));
                return new ExecutionProgress(configuration.getGetContextValue() + configuration.getSetContextValue());
            }
        }
        return null;
    }
}
