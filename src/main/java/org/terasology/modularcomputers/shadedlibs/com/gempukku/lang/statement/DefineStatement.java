// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.statement;

import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.DefiningExecutableStatement;
import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.Execution;
import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.ExecutionContext;
import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.ExecutionCostConfiguration;
import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.ExecutionException;
import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.ExecutionProgress;
import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.IllegalSyntaxException;
import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.execution.SimpleExecution;

public class DefineStatement implements DefiningExecutableStatement {
    private final String _name;

    public DefineStatement(String name) throws IllegalSyntaxException {
        _name = name;
    }

    @Override
    public String getDefinedVariableName() {
        return _name;
    }

    @Override
    public Execution createExecution() {
        return new SimpleExecution() {
            @Override
            protected ExecutionProgress execute(ExecutionContext context, ExecutionCostConfiguration configuration) throws ExecutionException {
                context.peekCallContext().defineVariable(_name);
                return new ExecutionProgress(configuration.getDefineVariable());
            }
        };
    }

    @Override
    public boolean requiresSemicolon() {
        return true;
    }
}
