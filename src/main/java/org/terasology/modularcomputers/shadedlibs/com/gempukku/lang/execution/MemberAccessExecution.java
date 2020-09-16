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

public class MemberAccessExecution implements Execution {
    private final int _line;
    private final ExecutableStatement _object;
    private final String _propertyName;

    private boolean _objectStacked;
    private boolean _objectResolved;

    private boolean _memberAccessStored;

    private Variable _objectValue;

    public MemberAccessExecution(int line, ExecutableStatement object, String propertyName) {
        _line = line;
        _object = object;
        _propertyName = propertyName;
    }

    @Override
    public boolean hasNextExecution(ExecutionContext executionContext) {
        if (!_objectStacked)
            return true;
        if (!_objectResolved)
            return true;
        return !_memberAccessStored;
    }

    @Override
    public ExecutionProgress executeNextStatement(ExecutionContext executionContext,
                                                  ExecutionCostConfiguration configuration) throws ExecutionException {
        if (!_objectStacked) {
            executionContext.stackExecution(_object.createExecution());
            _objectStacked = true;
            return new ExecutionProgress(configuration.getStackExecution());
        }
        if (!_objectResolved) {
            _objectValue = executionContext.getContextValue();
            _objectResolved = true;
            return new ExecutionProgress(configuration.getGetContextValue());
        }
        if (!_memberAccessStored) {
            final Variable member = executionContext.resolveMember(_objectValue, _propertyName);
            if (member == null)
                throw new ExecutionException(_line, "Property " + _propertyName + " not found");
            executionContext.setContextValue(member);
            _memberAccessStored = true;
            return new ExecutionProgress(configuration.getSetContextValue() + configuration.getResolveMember());
        }
        return null;
    }
}
