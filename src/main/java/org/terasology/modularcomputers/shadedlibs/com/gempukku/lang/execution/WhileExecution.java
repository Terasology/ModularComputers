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

public class WhileExecution implements Execution {
    private final int _line;
    private final ExecutableStatement _condition;
    private final ExecutableStatement _statement;

    private boolean _terminated;

    private boolean _conditionStacked;

    public WhileExecution(int line, ExecutableStatement condition, ExecutableStatement statement) {
        _line = line;
        _condition = condition;
        _statement = statement;
    }

    @Override
    public boolean hasNextExecution(ExecutionContext executionContext) {
        return !_terminated;
    }

    @Override
    public ExecutionProgress executeNextStatement(ExecutionContext executionContext,
                                                  ExecutionCostConfiguration configuration) throws ExecutionException {
        if (!_conditionStacked) {
            _conditionStacked = true;
            executionContext.stackExecution(_condition.createExecution());
            return new ExecutionProgress(configuration.getStackExecution());
        }
        final Variable value = executionContext.getContextValue();
        if (value.getType() != Variable.Type.BOOLEAN)
            throw new ExecutionException(_line, "Condition not of type BOOLEAN");
        final Boolean result = (Boolean) value.getValue();
        if (!result)
            _terminated = true;
        else {
            executionContext.stackExecution(_statement.createExecution());
            _conditionStacked = false;
        }
        if (_terminated)
            return new ExecutionProgress(configuration.getCompareValues() + configuration.getStackExecution());
        else
            return new ExecutionProgress(configuration.getStackExecution());
    }
}
