// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.execution;

import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.ExecutableStatement;
import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.Execution;
import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.ExecutionContext;
import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.ExecutionCostConfiguration;
import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.ExecutionException;
import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.ExecutionProgress;

public class ReturnExecution implements Execution {
    private final ExecutableStatement _result;

    private boolean _stackedExecution;
    private boolean _returnedResult;

    public ReturnExecution(ExecutableStatement result) {
        _result = result;
    }

    @Override
    public boolean hasNextExecution(ExecutionContext executionContext) {
        if (!_stackedExecution)
            return true;
        return !_returnedResult;
    }

    @Override
    public ExecutionProgress executeNextStatement(ExecutionContext executionContext,
                                                  ExecutionCostConfiguration configuration) throws ExecutionException {
        if (!_stackedExecution) {
            executionContext.stackExecution(_result.createExecution());
            _stackedExecution = true;
            return new ExecutionProgress(configuration.getStackExecution());
        }
        if (!_returnedResult) {
            executionContext.setReturnValue(executionContext.getContextValue());
            _returnedResult = true;
            return new ExecutionProgress(configuration.getGetContextValue() + configuration.getSetReturnValue());
        }
        return null;
    }
}
