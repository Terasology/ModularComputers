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

import java.util.ArrayList;
import java.util.List;

public class ListDefineExecution implements Execution {
    private final List<ExecutableStatement> _executableStatements;
    private int _nextStackIndex;
    private int _nextRetrieveIndex;

    private boolean _assignedResult;

    private final List<Variable> _result = new ArrayList<Variable>();

    public ListDefineExecution(List<ExecutableStatement> executableStatements) {
        _executableStatements = executableStatements;
    }

    @Override
    public boolean hasNextExecution(ExecutionContext executionContext) {
        return !_assignedResult;
    }

    @Override
    public ExecutionProgress executeNextStatement(ExecutionContext executionContext,
                                                  ExecutionCostConfiguration configuration) throws ExecutionException {
        if (_nextRetrieveIndex < _nextStackIndex) {
            _result.add(new Variable(executionContext.getContextValue().getValue()));
            _nextRetrieveIndex++;
            return new ExecutionProgress(configuration.getGetContextValue());
        }
        if (_nextStackIndex < _executableStatements.size()) {
            executionContext.stackExecution(_executableStatements.get(_nextStackIndex).createExecution());
            _nextStackIndex++;
            return new ExecutionProgress(configuration.getStackExecution());
        }
        if (!_assignedResult) {
            executionContext.setContextValue(new Variable(_result));
            _assignedResult = true;
            return new ExecutionProgress(configuration.getSetContextValue());
        }
        return null;
    }
}
