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

import java.util.ArrayList;
import java.util.List;

public class ListDefineExecution implements Execution {
    private List<ExecutableStatement> executableStatements;
    private int nextStackIndex;
    private int nextRetrieveIndex;

    private boolean assignedResult;

    private List<Variable> result = new ArrayList<Variable>();

    public ListDefineExecution(List<ExecutableStatement> executableStatements) {
        this.executableStatements = executableStatements;
    }

    @Override
    public boolean hasNextExecution(ExecutionContext executionContext) {
        return !assignedResult;
    }

    @Override
    public ExecutionProgress executeNextStatement(ExecutionContext executionContext, ExecutionCostConfiguration configuration)
            throws ExecutionException {
        if (nextRetrieveIndex < nextStackIndex) {
            result.add(new Variable(executionContext.getContextValue().getValue()));
            nextRetrieveIndex++;
            return new ExecutionProgress(configuration.getGetContextValue());
        }
        if (nextStackIndex < executableStatements.size()) {
            executionContext.stackExecution(executableStatements.get(nextStackIndex).createExecution());
            nextStackIndex++;
            return new ExecutionProgress(configuration.getStackExecution());
        }
        if (!assignedResult) {
            executionContext.setContextValue(new Variable(result));
            assignedResult = true;
            return new ExecutionProgress(configuration.getSetContextValue());
        }
        return null;
    }
}
