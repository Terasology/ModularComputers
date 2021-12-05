// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package com.gempukku.lang.statement;

import com.gempukku.lang.ExecutableStatement;
import com.gempukku.lang.Execution;
import com.gempukku.lang.execution.IncrementDecrementExecution;

public class IncrementDecrementStatement implements ExecutableStatement {
    private int line;
    private ExecutableStatement expression;
    private boolean increment;
    private boolean pre;

    public IncrementDecrementStatement(int line, ExecutableStatement expression, boolean increment, boolean pre) {
        this.line = line;
        this.expression = expression;
        this.increment = increment;
        this.pre = pre;
    }

    @Override
    public boolean requiresSemicolon() {
        return false;
    }

    @Override
    public Execution createExecution() {
        return new IncrementDecrementExecution(line, expression, increment, pre);
    }
}
