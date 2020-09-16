// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.statement;

import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.ExecutableStatement;
import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.Execution;
import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.execution.AddExecution;

public class AddStatement implements ExecutableStatement {
    private final int _line;
    private final ExecutableStatement _left;
    private final ExecutableStatement _right;
    private final boolean _assignToLeft;

    public AddStatement(int line, ExecutableStatement left, ExecutableStatement right, boolean assignToLeft) {
        _line = line;
        _left = left;
        _right = right;
        _assignToLeft = assignToLeft;
    }

    @Override
    public Execution createExecution() {
        return new AddExecution(_line, _left, _right, _assignToLeft);
    }

    @Override
    public boolean requiresSemicolon() {
        return true;
    }
}
