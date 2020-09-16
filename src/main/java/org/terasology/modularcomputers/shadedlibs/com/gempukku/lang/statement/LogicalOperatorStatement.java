// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.statement;

import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.ExecutableStatement;
import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.Execution;
import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.Operator;
import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.execution.LogicalOperatorExecution;

public class LogicalOperatorStatement implements ExecutableStatement {
    private final int _line;
    private final ExecutableStatement _left;
    private final Operator _operator;
    private final ExecutableStatement _right;

    public LogicalOperatorStatement(int line, ExecutableStatement left, Operator operator, ExecutableStatement right) {
        _line = line;
        _left = left;
        _operator = operator;
        _right = right;
    }

    @Override
    public Execution createExecution() {
        return new LogicalOperatorExecution(_line, _left, _operator, _right);
    }

    @Override
    public boolean requiresSemicolon() {
        return false;
    }
}
