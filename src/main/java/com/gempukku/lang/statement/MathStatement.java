// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package com.gempukku.lang.statement;

import com.gempukku.lang.ExecutableStatement;
import com.gempukku.lang.Execution;
import com.gempukku.lang.Operator;
import com.gempukku.lang.execution.MathExecution;

public class MathStatement implements ExecutableStatement {
    private final int _line;
    private final ExecutableStatement _left;
    private final Operator _operator;
    private final ExecutableStatement _right;
    private final boolean _assignToLeft;

    public MathStatement(int line, ExecutableStatement left, Operator operator, ExecutableStatement right,
                         boolean assignToLeft) {
        _line = line;
        _left = left;
        _operator = operator;
        _right = right;
        _assignToLeft = assignToLeft;
    }

    @Override
    public Execution createExecution() {
        return new MathExecution(_line, _left, _operator, _right, _assignToLeft);
    }

    @Override
    public boolean requiresSemicolon() {
        return false;
    }
}
