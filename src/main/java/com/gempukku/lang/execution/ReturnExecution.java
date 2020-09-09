// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package com.gempukku.lang.execution;

import com.gempukku.lang.ExecutableStatement;
import com.gempukku.lang.Execution;
import com.gempukku.lang.ExecutionContext;
import com.gempukku.lang.ExecutionCostConfiguration;
import com.gempukku.lang.ExecutionException;
import com.gempukku.lang.ExecutionProgress;

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
