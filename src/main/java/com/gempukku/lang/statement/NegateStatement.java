// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package com.gempukku.lang.statement;

import com.gempukku.lang.ExecutableStatement;
import com.gempukku.lang.Execution;
import com.gempukku.lang.execution.NegateExecution;

public class NegateStatement implements ExecutableStatement {
    private int line;
    private ExecutableStatement expression;

    public NegateStatement(int line, ExecutableStatement expression) {
        this.line = line;
        this.expression = expression;
    }

    @Override
    public Execution createExecution() {
        return new NegateExecution(line, expression);
    }

    @Override
    public boolean requiresSemicolon() {
        return false;
    }
}
