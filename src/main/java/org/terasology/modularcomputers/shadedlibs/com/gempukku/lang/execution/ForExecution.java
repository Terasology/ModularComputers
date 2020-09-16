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

public class ForExecution implements Execution {
    private final int _line;
    private final ExecutableStatement _initializationStatement;
    private final ExecutableStatement _terminationCondition;
    private final ExecutableStatement _executedAfterEachLoop;
    private final ExecutableStatement _statementInLoop;

    private boolean _terminated;

    private boolean _initialized;
    private boolean _conditionStacked;
    private boolean _conditionChecked;

    private boolean _statementStacked;

    public ForExecution(int line, ExecutableStatement initializationStatement,
                        ExecutableStatement terminationCondition, ExecutableStatement executedAfterEachLoop,
                        ExecutableStatement statementInLoop) {
        _line = line;
        _initializationStatement = initializationStatement;
        _terminationCondition = terminationCondition;
        _executedAfterEachLoop = executedAfterEachLoop;
        _statementInLoop = statementInLoop;
    }

    @Override
    public boolean hasNextExecution(ExecutionContext executionContext) {
        return !_terminated;
    }

    @Override
    public ExecutionProgress executeNextStatement(ExecutionContext executionContext,
                                                  ExecutionCostConfiguration configuration) throws ExecutionException {
        if (!_initialized) {
            _initialized = true;
            if (_initializationStatement != null) {
                executionContext.stackExecution(_initializationStatement.createExecution());
                return new ExecutionProgress(configuration.getStackExecution());
            }
        }
        if (!_conditionStacked) {
            executionContext.stackExecution(_terminationCondition.createExecution());
            _conditionStacked = true;
            return new ExecutionProgress(configuration.getStackExecution());
        }
        if (!_conditionChecked) {
            final Variable value = executionContext.getContextValue();
            if (value.getType() != Variable.Type.BOOLEAN)
                throw new ExecutionException(_line, "Condition not of type BOOLEAN");
            if (!(Boolean) value.getValue())
                _terminated = true;
            _conditionChecked = true;
            return new ExecutionProgress(configuration.getGetContextValue() + configuration.getCompareValues());
        }
        if (!_statementStacked) {
            _statementStacked = true;
            if (_statementInLoop != null) {
                executionContext.stackExecution(_statementInLoop.createExecution());
                return new ExecutionProgress(configuration.getStackExecution());
            }
        }
        if (_executedAfterEachLoop != null)
            executionContext.stackExecution(_executedAfterEachLoop.createExecution());
        _conditionStacked = false;
        _conditionChecked = false;
        _statementStacked = false;
        return new ExecutionProgress(configuration.getStackExecution());
    }
}
