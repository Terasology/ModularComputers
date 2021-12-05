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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MapDefineExecution implements Execution {
    private Iterator<Map.Entry<String, ExecutableStatement>> propertiesIterator;
    private String lastKey;
    private boolean hasToAssign;

    private boolean finished;
    private Map<String, Variable> result = new HashMap<String, Variable>();

    public MapDefineExecution(Map<String, ExecutableStatement> properties) {
        propertiesIterator = properties.entrySet().iterator();
    }

    @Override
    public boolean hasNextExecution(ExecutionContext executionContext) {
        return !finished;
    }

    @Override
    public ExecutionProgress executeNextStatement(ExecutionContext executionContext, ExecutionCostConfiguration configuration)
            throws ExecutionException {
        if (hasToAssign) {
            result.put(lastKey, new Variable(executionContext.getContextValue().getValue()));
            hasToAssign = false;
            return new ExecutionProgress(configuration.getGetContextValue());
        }
        if (propertiesIterator.hasNext()) {
            final Map.Entry<String, ExecutableStatement> property = propertiesIterator.next();
            lastKey = property.getKey();
            hasToAssign = true;
            executionContext.stackExecution(property.getValue().createExecution());
            return new ExecutionProgress(configuration.getStackExecution());
        }
        if (!finished) {
            finished = true;
            executionContext.setContextValue(new Variable(result));
            return new ExecutionProgress(configuration.getSetContextValue());
        }
        return null;
    }
}
