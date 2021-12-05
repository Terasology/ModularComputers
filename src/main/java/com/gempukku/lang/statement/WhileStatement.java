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
import com.gempukku.lang.execution.SimpleExecution;
import com.gempukku.lang.execution.WhileExecution;

public class WhileStatement implements ExecutableStatement {
    private int line;
    private ExecutableStatement condition;
    private ExecutableStatement statement;

    public WhileStatement(int line, ExecutableStatement condition, ExecutableStatement statement) {
        this.line = line;
        this.condition = condition;
        this.statement = statement;
    }

    @Override
    public Execution createExecution() {
        return new SimpleExecution() {
            @Override
            protected ExecutionProgress execute(ExecutionContext context, ExecutionCostConfiguration configuration)
                    throws ExecutionException {
                CallContext whileContext = new CallContext(context.peekCallContext(), true, false);
                context.stackExecutionGroup(whileContext,
                        new WhileExecution(line, condition, statement));
                return new ExecutionProgress(configuration.getStackGroupExecution());
            }
        };
    }

    @Override
    public boolean requiresSemicolon() {
        return false;
    }
}
