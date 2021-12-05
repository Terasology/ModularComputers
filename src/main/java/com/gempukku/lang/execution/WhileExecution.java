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

public class WhileExecution implements Execution {
    private int line;
    private ExecutableStatement condition;
    private ExecutableStatement statement;

    private boolean terminated;

    private boolean conditionStacked;

    public WhileExecution(int line, ExecutableStatement condition, ExecutableStatement statement) {
        this.line = line;
        this.condition = condition;
        this.statement = statement;
    }

    @Override
    public boolean hasNextExecution(ExecutionContext executionContext) {
        return !terminated;
    }

    @Override
    public ExecutionProgress executeNextStatement(ExecutionContext executionContext, ExecutionCostConfiguration configuration)
            throws ExecutionException {
        if (!conditionStacked) {
            conditionStacked = true;
            executionContext.stackExecution(condition.createExecution());
            return new ExecutionProgress(configuration.getStackExecution());
        }
        final Variable value = executionContext.getContextValue();
        if (value.getType() != Variable.Type.BOOLEAN) {
            throw new ExecutionException(line, "Condition not of type BOOLEAN");
        }
        final Boolean result = (Boolean) value.getValue();
        if (!result) {
            terminated = true;
        } else {
            executionContext.stackExecution(statement.createExecution());
            conditionStacked = false;
        }
        if (terminated) {
            return new ExecutionProgress(configuration.getCompareValues() + configuration.getStackExecution());
        } else {
            return new ExecutionProgress(configuration.getStackExecution());
        }
    }
}
