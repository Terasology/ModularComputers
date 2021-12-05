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

public class ForExecution implements Execution {
    private int line;
    private ExecutableStatement initializationStatement;
    private ExecutableStatement terminationCondition;
    private ExecutableStatement executedAfterEachLoop;
    private ExecutableStatement statementInLoop;

    private boolean terminated;

    private boolean initialized;
    private boolean conditionStacked;
    private boolean conditionChecked;

    private boolean statementStacked;

    public ForExecution(int line, ExecutableStatement initializationStatement, ExecutableStatement terminationCondition,
                        ExecutableStatement executedAfterEachLoop, ExecutableStatement statementInLoop) {
        this.line = line;
        this.initializationStatement = initializationStatement;
        this.terminationCondition = terminationCondition;
        this.executedAfterEachLoop = executedAfterEachLoop;
        this.statementInLoop = statementInLoop;
    }

    @Override
    public boolean hasNextExecution(ExecutionContext executionContext) {
        return !terminated;
    }

    @Override
    public ExecutionProgress executeNextStatement(ExecutionContext executionContext, ExecutionCostConfiguration configuration)
            throws ExecutionException {
        if (!initialized) {
            initialized = true;
            if (initializationStatement != null) {
                executionContext.stackExecution(initializationStatement.createExecution());
                return new ExecutionProgress(configuration.getStackExecution());
            }
        }
        if (!conditionStacked) {
            executionContext.stackExecution(terminationCondition.createExecution());
            conditionStacked = true;
            return new ExecutionProgress(configuration.getStackExecution());
        }
        if (!conditionChecked) {
            final Variable value = executionContext.getContextValue();
            if (value.getType() != Variable.Type.BOOLEAN) {
                throw new ExecutionException(line, "Condition not of type BOOLEAN");
            }
            if (!(Boolean) value.getValue()) {
                terminated = true;
            }
            conditionChecked = true;
            return new ExecutionProgress(configuration.getGetContextValue() + configuration.getCompareValues());
        }
        if (!statementStacked) {
            statementStacked = true;
            if (statementInLoop != null) {
                executionContext.stackExecution(statementInLoop.createExecution());
                return new ExecutionProgress(configuration.getStackExecution());
            }
        }
        if (executedAfterEachLoop != null) {
            executionContext.stackExecution(executedAfterEachLoop.createExecution());
        }
        conditionStacked = false;
        conditionChecked = false;
        statementStacked = false;
        return new ExecutionProgress(configuration.getStackExecution());
    }
}
