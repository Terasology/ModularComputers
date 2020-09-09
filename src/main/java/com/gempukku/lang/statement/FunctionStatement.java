// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package com.gempukku.lang.statement;

import com.gempukku.lang.DefaultFunctionExecutable;
import com.gempukku.lang.ExecutableStatement;
import com.gempukku.lang.Execution;
import com.gempukku.lang.ExecutionContext;
import com.gempukku.lang.ExecutionCostConfiguration;
import com.gempukku.lang.ExecutionException;
import com.gempukku.lang.ExecutionProgress;
import com.gempukku.lang.Variable;
import com.gempukku.lang.execution.SimpleExecution;

import java.util.List;

public class FunctionStatement implements ExecutableStatement {
    private final List<String> _parameterNames;
    private final List<ExecutableStatement> _statements;

    public FunctionStatement(List<String> parameterNames, List<ExecutableStatement> statements) {
        _parameterNames = parameterNames;
        _statements = statements;
    }

    @Override
    public Execution createExecution() {
        return new SimpleExecution() {
            @Override
            protected ExecutionProgress execute(ExecutionContext context, ExecutionCostConfiguration configuration) throws ExecutionException {
                final DefaultFunctionExecutable functionExecutable =
                        new DefaultFunctionExecutable(context.peekCallContext(),
                        _parameterNames);
                functionExecutable.setStatement(
                        new BlockStatement(_statements, false, true));
                context.setContextValue(new Variable(functionExecutable));
                return new ExecutionProgress(configuration.getSetVariable());
            }
        };
    }

    @Override
    public boolean requiresSemicolon() {
        return false;
    }
}
