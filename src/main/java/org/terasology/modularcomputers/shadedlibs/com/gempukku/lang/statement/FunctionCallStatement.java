// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.statement;

import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.ExecutableStatement;
import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.Execution;
import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.execution.FunctionCallExecution;

import java.util.List;

public class FunctionCallStatement implements ExecutableStatement {
    private final int _line;
    private final ExecutableStatement _function;
    private final List<ExecutableStatement> _parameters;

    public FunctionCallStatement(int line, ExecutableStatement function, List<ExecutableStatement> parameters) {
        _line = line;
        _function = function;
        _parameters = parameters;
    }

    @Override
    public Execution createExecution() {
        return new FunctionCallExecution(_line, _function, _parameters);
    }

    @Override
    public boolean requiresSemicolon() {
        return true;
    }
}
