// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package com.gempukku.lang.statement;

import com.gempukku.lang.ExecutableStatement;
import com.gempukku.lang.Execution;
import com.gempukku.lang.ExecutionContext;
import com.gempukku.lang.ExecutionCostConfiguration;
import com.gempukku.lang.ExecutionException;
import com.gempukku.lang.ExecutionProgress;
import com.gempukku.lang.IllegalSyntaxException;
import com.gempukku.lang.execution.SimpleExecution;

public class VariableStatement implements ExecutableStatement {
    private String name;

    public VariableStatement(String name) throws IllegalSyntaxException {
        this.name = name;
    }

    public Execution createExecution() {
        return new SimpleExecution() {
            @Override
            protected ExecutionProgress execute(ExecutionContext context, ExecutionCostConfiguration configuration)
                    throws ExecutionException {
                context.setContextValue(context.peekCallContext().getVariableValue(name));
                return new ExecutionProgress(configuration.getSetContextValue());
            }
        };
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean requiresSemicolon() {
        return true;
    }
}
