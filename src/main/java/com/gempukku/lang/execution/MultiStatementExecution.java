// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package com.gempukku.lang.execution;

import com.gempukku.lang.ExecutableStatement;
import com.gempukku.lang.Execution;
import com.gempukku.lang.ExecutionContext;
import com.gempukku.lang.ExecutionCostConfiguration;
import com.gempukku.lang.ExecutionProgress;

import java.util.List;

public class MultiStatementExecution implements Execution {
    private final List<ExecutableStatement> _statements;
    private int _nextIndex = 0;

    public MultiStatementExecution(List<ExecutableStatement> statements) {
        _statements = statements;
    }

    public boolean hasNextExecution(ExecutionContext executionContext) {
        return _nextIndex < _statements.size();
    }

    public ExecutionProgress executeNextStatement(ExecutionContext executionContext,
                                                  ExecutionCostConfiguration configuration) {
        final ExecutableStatement executableStatement = _statements.get(_nextIndex);
        Execution execution = executableStatement.createExecution();
        executionContext.stackExecution(execution);
        _nextIndex++;
        return new ExecutionProgress(configuration.getStackExecution());
    }
}
