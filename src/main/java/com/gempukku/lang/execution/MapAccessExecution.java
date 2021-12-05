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

import java.util.List;
import java.util.Map;

public class MapAccessExecution implements Execution {
    private int line;
    private ExecutableStatement mapStatement;
    private ExecutableStatement propertyStatement;

    private boolean stackedMapStatement;
    private boolean resolvedMapStatement;
    private boolean stackedPropertyStatement;
    private boolean assignedValue;

    private Variable mapVariable;

    public MapAccessExecution(int line, ExecutableStatement mapStatement, ExecutableStatement propertyStatement) {
        this.line = line;
        this.mapStatement = mapStatement;
        this.propertyStatement = propertyStatement;
    }

    @Override
    public boolean hasNextExecution(ExecutionContext executionContext) {
        if (!stackedMapStatement) {
            return true;
        }
        if (!resolvedMapStatement) {
            return true;
        }
        if (!stackedPropertyStatement) {
            return true;
        }
        return !assignedValue;
    }

    @Override
    public ExecutionProgress executeNextStatement(ExecutionContext executionContext, ExecutionCostConfiguration configuration)
            throws ExecutionException {
        if (!stackedMapStatement) {
            stackedMapStatement = true;
            executionContext.stackExecution(mapStatement.createExecution());
            return new ExecutionProgress(configuration.getStackExecution());
        }
        if (!resolvedMapStatement) {
            resolvedMapStatement = true;
            mapVariable = executionContext.getContextValue();
            if (mapVariable.getType() != Variable.Type.MAP && mapVariable.getType() != Variable.Type.LIST) {
                throw new ExecutionException(line, "Map or list expected");
            }
            return new ExecutionProgress(configuration.getGetContextValue());
        }
        if (!stackedPropertyStatement) {
            stackedPropertyStatement = true;
            executionContext.stackExecution(propertyStatement.createExecution());
            return new ExecutionProgress(configuration.getStackExecution());
        }
        if (!assignedValue) {
            assignedValue = true;
            final Variable value = executionContext.getContextValue();
            if (mapVariable.getType() == Variable.Type.MAP) {
                if (value.getType() != Variable.Type.STRING) {
                    throw new ExecutionException(line, "Property name expected");
                }
                Map<String, Variable> properties = (Map<String, Variable>) mapVariable.getValue();
                final String propertyName = (String) value.getValue();
                if (!properties.containsKey(propertyName)) {
                    properties.put(propertyName, new Variable(null));
                }
                executionContext.setContextValue(properties.get(propertyName));
            } else {
                if (value.getType() != Variable.Type.NUMBER) {
                    throw new ExecutionException(line, "List index expected");
                }
                List<Variable> values = (List<Variable>) mapVariable.getValue();
                int index = ((Number) value.getValue()).intValue();
                if (index < 0 || index >= values.size()) {
                    throw new ExecutionException(line, "List index out of bounds");
                }
                executionContext.setContextValue(values.get(index));
            }
            return new ExecutionProgress(configuration.getGetContextValue() + configuration.getSetContextValue());
        }
        return null;
    }
}
