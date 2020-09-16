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
import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.execution.ForExecution;
import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.execution.SimpleExecution;

public class ForStatement implements ExecutableStatement {
    private final int _line;
    private final ExecutableStatement _initializationStatement;
    private final ExecutableStatement _terminationCondition;
    private final ExecutableStatement _executedAfterEachLoop;
    private final ExecutableStatement _statementInLoop;

    public ForStatement(int line, ExecutableStatement initializationStatement,
                        ExecutableStatement terminationCondition, ExecutableStatement executedAfterEachLoop,
                        ExecutableStatement statementInLoop) {
        _line = line;
        _initializationStatement = initializationStatement;
        _terminationCondition = terminationCondition;
        _executedAfterEachLoop = executedAfterEachLoop;
        _statementInLoop = statementInLoop;
    }

    @Override
    public Execution createExecution() {
        return new SimpleExecution() {
            @Override
            protected ExecutionProgress execute(ExecutionContext context, ExecutionCostConfiguration configuration) throws ExecutionException {
                CallContext forContext = new CallContext(context.peekCallContext(), true, false);
                context.stackExecutionGroup(forContext,
                        new ForExecution(_line, _initializationStatement, _terminationCondition,
                                _executedAfterEachLoop, _statementInLoop));
                return new ExecutionProgress(configuration.getStackGroupExecution());
            }
        };
    }

    @Override
    public boolean requiresSemicolon() {
        return false;
    }
}
