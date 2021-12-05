// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package com.gempukku.lang.statement;

import com.gempukku.lang.CallContext;
import com.gempukku.lang.ExecutableStatement;
import com.gempukku.lang.Execution;
import com.gempukku.lang.ExecutionContext;
import com.gempukku.lang.ExecutionCostConfiguration;
import com.gempukku.lang.ExecutionException;
import com.gempukku.lang.ExecutionProgress;
import com.gempukku.lang.execution.ForExecution;
import com.gempukku.lang.execution.SimpleExecution;

public class ForStatement implements ExecutableStatement {
    private int line;
    private ExecutableStatement initializationStatement;
    private ExecutableStatement terminationCondition;
    private ExecutableStatement executedAfterEachLoop;
    private ExecutableStatement statementInLoop;

    public ForStatement(int line, ExecutableStatement initializationStatement, ExecutableStatement terminationCondition,
                        ExecutableStatement executedAfterEachLoop, ExecutableStatement statementInLoop) {
        this.line = line;
        this.initializationStatement = initializationStatement;
        this.terminationCondition = terminationCondition;
        this.executedAfterEachLoop = executedAfterEachLoop;
        this.statementInLoop = statementInLoop;
    }

    @Override
    public Execution createExecution() {
        return new SimpleExecution() {
            @Override
            protected ExecutionProgress execute(ExecutionContext context, ExecutionCostConfiguration configuration)
                    throws ExecutionException {
                CallContext forContext = new CallContext(context.peekCallContext(), true, false);
                context.stackExecutionGroup(forContext,
                        new ForExecution(line, initializationStatement, terminationCondition, executedAfterEachLoop, statementInLoop));
                return new ExecutionProgress(configuration.getStackGroupExecution());
            }
        };
    }

    @Override
    public boolean requiresSemicolon() {
        return false;
    }
}
