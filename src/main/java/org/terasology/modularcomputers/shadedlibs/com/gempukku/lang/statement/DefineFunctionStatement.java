// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.statement;

import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.CallContext;
import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.DefaultFunctionExecutable;
import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.DefiningExecutableStatement;
import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.ExecutableStatement;
import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.Execution;
import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.ExecutionContext;
import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.ExecutionCostConfiguration;
import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.ExecutionException;
import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.ExecutionProgress;
import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.IllegalSyntaxException;
import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.Variable;
import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.execution.SimpleExecution;

import java.util.List;

public class DefineFunctionStatement implements DefiningExecutableStatement {
    private final String _name;
    private final List<String> _parameterNames;
    private final List<ExecutableStatement> _statements;

    public DefineFunctionStatement(String name, List<String> parameterNames, List<ExecutableStatement> statements) throws IllegalSyntaxException {
        _name = name;
        _parameterNames = parameterNames;
        _statements = statements;
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
                final DefaultFunctionExecutable functionExecutable =
                        new DefaultFunctionExecutable(context.peekCallContext(), _parameterNames);
                functionExecutable.setStatement(
                        new BlockStatement(_statements, false, true));
                final CallContext callContext = context.peekCallContext();
                final Variable variable = callContext.defineVariable(_name);
                context.setVariableValue(variable, functionExecutable);
                return new ExecutionProgress(configuration.getSetVariable());
            }
        };
    }

    @Override
    public boolean requiresSemicolon() {
        return false;
    }
}
