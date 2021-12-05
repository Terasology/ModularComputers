// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package com.gempukku.lang.statement;

import com.gempukku.lang.DefiningExecutableStatement;
import com.gempukku.lang.Execution;
import com.gempukku.lang.ExecutionContext;
import com.gempukku.lang.ExecutionCostConfiguration;
import com.gempukku.lang.ExecutionException;
import com.gempukku.lang.ExecutionProgress;
import com.gempukku.lang.IllegalSyntaxException;
import com.gempukku.lang.execution.SimpleExecution;

public class DefineStatement implements DefiningExecutableStatement {
    private String name;

    public DefineStatement(String name) throws IllegalSyntaxException {
        this.name = name;
    }

    @Override
    public String getDefinedVariableName() {
        return name;
    }

    @Override
    public Execution createExecution() {
        return new SimpleExecution() {
            @Override
            protected ExecutionProgress execute(ExecutionContext context, ExecutionCostConfiguration configuration)
                    throws ExecutionException {
                context.peekCallContext().defineVariable(name);
                return new ExecutionProgress(configuration.getDefineVariable());
            }
        };
    }

    @Override
    public boolean requiresSemicolon() {
        return true;
    }
}
