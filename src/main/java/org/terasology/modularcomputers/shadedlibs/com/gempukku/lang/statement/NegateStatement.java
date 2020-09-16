// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.statement;

import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.ExecutableStatement;
import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.Execution;
import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.execution.NegateExecution;

public class NegateStatement implements ExecutableStatement {
    private final int _line;
    private final ExecutableStatement _expression;

    public NegateStatement(int line, ExecutableStatement expression) {
        _line = line;
        _expression = expression;
    }

    @Override
    public Execution createExecution() {
        return new NegateExecution(_line, _expression);
    }

    @Override
    public boolean requiresSemicolon() {
        return false;
    }
}
