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
import com.gempukku.lang.execution.MultiStatementExecution;

import java.util.List;

public class BlockStatement implements ExecutableStatement {
    private List<ExecutableStatement> statements;
    private boolean consumesBreak;
    private boolean consumesReturn;

    public BlockStatement(List<ExecutableStatement> statements, boolean consumesBreak, boolean consumesReturn) {
        this.statements = statements;
        this.consumesBreak = consumesBreak;
        this.consumesReturn = consumesReturn;
    }

    public Execution createExecution() {
        return new Execution() {
            private boolean stacked;

            @Override
            public boolean hasNextExecution(ExecutionContext executionContext) {
                return !stacked;
            }

            @Override
            public ExecutionProgress executeNextStatement(ExecutionContext executionContext, ExecutionCostConfiguration configuration)
                    throws ExecutionException {
                CallContext blockContext = new CallContext(executionContext.peekCallContext(), consumesBreak, consumesReturn);
                executionContext.stackExecutionGroup(blockContext, new MultiStatementExecution(statements));
                stacked = true;
                return new ExecutionProgress(configuration.getStackGroupExecution());
            }
        };
    }

    @Override
    public boolean requiresSemicolon() {
        return true;
    }
}
