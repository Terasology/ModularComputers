// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.execution;

import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.Execution;
import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.ExecutionContext;
import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.ExecutionCostConfiguration;
import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.ExecutionException;
import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.ExecutionProgress;

public abstract class SimpleExecution implements Execution {
    private boolean _executed;

    public boolean hasNextExecution(ExecutionContext executionContext) {
        return !_executed;
    }

    public ExecutionProgress executeNextStatement(ExecutionContext executionContext,
                                                  ExecutionCostConfiguration configuration) throws ExecutionException {
        final ExecutionProgress result = execute(executionContext, configuration);
        _executed = true;
        return result;
    }

    protected abstract ExecutionProgress execute(ExecutionContext context, ExecutionCostConfiguration configuration) throws ExecutionException;
}
