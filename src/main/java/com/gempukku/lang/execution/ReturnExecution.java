// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package com.gempukku.lang.execution;

import com.gempukku.lang.ExecutableStatement;
import com.gempukku.lang.Execution;
import com.gempukku.lang.ExecutionContext;
import com.gempukku.lang.ExecutionCostConfiguration;
import com.gempukku.lang.ExecutionException;
import com.gempukku.lang.ExecutionProgress;

public class ReturnExecution implements Execution {
    private ExecutableStatement result;

    private boolean stackedExecution;
    private boolean returnedResult;

    public ReturnExecution(ExecutableStatement result) {
        this.result = result;
    }

    @Override
    public boolean hasNextExecution(ExecutionContext executionContext) {
        if (!stackedExecution) {
            return true;
        }
        return !returnedResult;
    }

    @Override
    public ExecutionProgress executeNextStatement(ExecutionContext executionContext, ExecutionCostConfiguration configuration)
            throws ExecutionException {
        if (!stackedExecution) {
            executionContext.stackExecution(result.createExecution());
            stackedExecution = true;
            return new ExecutionProgress(configuration.getStackExecution());
        }
        if (!returnedResult) {
            executionContext.setReturnValue(executionContext.getContextValue());
            returnedResult = true;
            return new ExecutionProgress(configuration.getGetContextValue() + configuration.getSetReturnValue());
        }
        return null;
    }
}
