// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.statement;

import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.ExecutableStatement;
import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.Execution;
import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.execution.IncrementDecrementExecution;

public class IncrementDecrementStatement implements ExecutableStatement {
    private final int _line;
    private final ExecutableStatement _expression;
    private final boolean _increment;
    private final boolean _pre;

    public IncrementDecrementStatement(int line, ExecutableStatement expression, boolean increment, boolean pre) {
        _line = line;
        _expression = expression;
        _increment = increment;
        _pre = pre;
    }

    @Override
    public boolean requiresSemicolon() {
        return false;
    }

    @Override
    public Execution createExecution() {
        return new IncrementDecrementExecution(_line, _expression, _increment, _pre);
    }
}
