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

public class MemberAccessExecution implements Execution {
    private int line;
    private ExecutableStatement object;
    private String propertyName;

    private boolean objectStacked;
    private boolean objectResolved;

    private boolean memberAccessStored;

    private Variable objectValue;

    public MemberAccessExecution(int line, ExecutableStatement object, String propertyName) {
        this.line = line;
        this.object = object;
        this.propertyName = propertyName;
    }

    @Override
    public boolean hasNextExecution(ExecutionContext executionContext) {
        if (!objectStacked) {
            return true;
        }
        if (!objectResolved) {
            return true;
        }
        return !memberAccessStored;
    }

    @Override
    public ExecutionProgress executeNextStatement(ExecutionContext executionContext, ExecutionCostConfiguration configuration)
            throws ExecutionException {
        if (!objectStacked) {
            executionContext.stackExecution(object.createExecution());
            objectStacked = true;
            return new ExecutionProgress(configuration.getStackExecution());
        }
        if (!objectResolved) {
            objectValue = executionContext.getContextValue();
            objectResolved = true;
            return new ExecutionProgress(configuration.getGetContextValue());
        }
        if (!memberAccessStored) {
            final Variable member = executionContext.resolveMember(objectValue, propertyName);
            if (member == null) {
                throw new ExecutionException(line, "Property " + propertyName + " not found");
            }
            executionContext.setContextValue(member);
            memberAccessStored = true;
            return new ExecutionProgress(configuration.getSetContextValue() + configuration.getResolveMember());
        }
        return null;
    }
}
