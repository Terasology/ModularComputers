// Copyright 2021 The Terasology Foundation
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
    private List<String> parameterNames;
    private List<ExecutableStatement> statements;

    public FunctionStatement(List<String> parameterNames, List<ExecutableStatement> statements) {
        this.parameterNames = parameterNames;
        this.statements = statements;
    }

    @Override
    public Execution createExecution() {
        return new SimpleExecution() {
            @Override
            protected ExecutionProgress execute(ExecutionContext context, ExecutionCostConfiguration configuration)
                    throws ExecutionException {
                final DefaultFunctionExecutable functionExecutable = new DefaultFunctionExecutable(context.peekCallContext(),
                        parameterNames);
                functionExecutable.setStatement(
                        new BlockStatement(statements, false, true));
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
