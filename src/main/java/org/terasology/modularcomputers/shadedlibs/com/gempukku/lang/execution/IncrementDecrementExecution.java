// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.execution;

import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.ExecutableStatement;
import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.Execution;
import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.ExecutionContext;
import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.ExecutionCostConfiguration;
import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.ExecutionException;
import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.ExecutionProgress;
import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.Variable;

public class IncrementDecrementExecution implements Execution {
    private final int _line;
    private final ExecutableStatement _expression;
    private final boolean _increment;
    private final boolean _pre;

    private boolean _stackedExecution;
    private boolean _finished;

    public IncrementDecrementExecution(int line, ExecutableStatement expression, boolean increment, boolean pre) {
        _line = line;
        _expression = expression;
        _increment = increment;
        _pre = pre;
    }

    @Override
    public boolean hasNextExecution(ExecutionContext executionContext) {
        return !_finished;
    }

    @Override
    public ExecutionProgress executeNextStatement(ExecutionContext executionContext,
                                                  ExecutionCostConfiguration configuration) throws ExecutionException {
        if (!_stackedExecution) {
            executionContext.stackExecution(_expression.createExecution());
            _stackedExecution = true;
            return new ExecutionProgress(configuration.getStackExecution());
        }
        final Variable contextValue = executionContext.getContextValue();
        if (contextValue.getType() != Variable.Type.NUMBER)
            throw new ExecutionException(_line, "Expected NUMBER");

        final float original = ((Number) contextValue.getValue()).floatValue();
        float result = original;
        if (_pre)
            if (_increment)
                result += 1;
            else
                result -= 1;
        if (_increment)
            contextValue.setValue(original + 1);
        else
            contextValue.setValue(original - 1);
        executionContext.setContextValue(new Variable(result));
        _finished = true;

        return new ExecutionProgress(configuration.getGetContextValue() + configuration.getSetContextValue());
    }
}
