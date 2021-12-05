// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package com.gempukku.lang.statement;

import com.gempukku.lang.ExecutableStatement;
import com.gempukku.lang.Execution;
import com.gempukku.lang.Operator;
import com.gempukku.lang.execution.MathExecution;

public class MathStatement implements ExecutableStatement {
    private int line;
    private ExecutableStatement left;
    private Operator operator;
    private ExecutableStatement right;
    private boolean assignToLeft;

    public MathStatement(int line, ExecutableStatement left, Operator operator, ExecutableStatement right, boolean assignToLeft) {
        this.line = line;
        this.left = left;
        this.operator = operator;
        this.right = right;
        this.assignToLeft = assignToLeft;
    }

    @Override
    public Execution createExecution() {
        return new MathExecution(line, left, operator, right, assignToLeft);
    }

    @Override
    public boolean requiresSemicolon() {
        return false;
    }
}
