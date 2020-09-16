// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.statement;

import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.CallContext;
import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.ExecutableStatement;
import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.Execution;
import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.ExecutionContext;
import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.ExecutionCostConfiguration;
import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.ExecutionException;
import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.ExecutionProgress;
import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.execution.MultiStatementExecution;

import java.util.List;

public class BlockStatement implements ExecutableStatement {
    private final List<ExecutableStatement> _statements;
    private final boolean _consumesBreak;
    private final boolean _consumesReturn;

    public BlockStatement(List<ExecutableStatement> statements, boolean consumesBreak, boolean consumesReturn) {
        _statements = statements;
        _consumesBreak = consumesBreak;
        _consumesReturn = consumesReturn;
    }

    public Execution createExecution() {
        return new Execution() {
            private boolean _stacked;

            @Override
            public boolean hasNextExecution(ExecutionContext executionContext) {
                return !_stacked;
            }

            @Override
            public ExecutionProgress executeNextStatement(ExecutionContext executionContext,
                                                          ExecutionCostConfiguration configuration) throws ExecutionException {
                CallContext blockContext = new CallContext(executionContext.peekCallContext(), _consumesBreak,
                        _consumesReturn);
                executionContext.stackExecutionGroup(blockContext, new MultiStatementExecution(_statements));
                _stacked = true;
                return new ExecutionProgress(configuration.getStackGroupExecution());
            }
        };
    }

    @Override
    public boolean requiresSemicolon() {
        return true;
    }
}
