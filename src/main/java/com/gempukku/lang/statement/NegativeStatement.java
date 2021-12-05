// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package com.gempukku.lang.statement;

import com.gempukku.lang.ExecutableStatement;
import com.gempukku.lang.Execution;
import com.gempukku.lang.execution.NegativeExecution;

public class NegativeStatement implements ExecutableStatement {
    private int line;
    private ExecutableStatement expression;

    public NegativeStatement(int line, ExecutableStatement expression) {
        this.line = line;
        this.expression = expression;
    }

    @Override
    public Execution createExecution() {
        return new NegativeExecution(line, expression);
    }

    @Override
    public boolean requiresSemicolon() {
        return false;
    }
}
