// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package com.gempukku.lang.execution;

import com.gempukku.lang.ExecutableStatement;
import com.gempukku.lang.Execution;
import com.gempukku.lang.ExecutionContext;
import com.gempukku.lang.ExecutionCostConfiguration;
import com.gempukku.lang.ExecutionProgress;

import java.util.List;

public class MultiStatementExecution implements Execution {
    private List<ExecutableStatement> statements;
    private int nextIndex = 0;

    public MultiStatementExecution(List<ExecutableStatement> statements) {
        this.statements = statements;
    }

    public boolean hasNextExecution(ExecutionContext executionContext) {
        return nextIndex < statements.size();
    }

    public ExecutionProgress executeNextStatement(ExecutionContext executionContext, ExecutionCostConfiguration configuration) {
        final ExecutableStatement executableStatement = statements.get(nextIndex);
        Execution execution = executableStatement.createExecution();
        executionContext.stackExecution(execution);
        nextIndex++;
        return new ExecutionProgress(configuration.getStackExecution());
    }
}
