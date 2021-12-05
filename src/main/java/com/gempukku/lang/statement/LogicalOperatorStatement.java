// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package com.gempukku.lang.statement;

import com.gempukku.lang.ExecutableStatement;
import com.gempukku.lang.Execution;
import com.gempukku.lang.Operator;
import com.gempukku.lang.execution.LogicalOperatorExecution;

public class LogicalOperatorStatement implements ExecutableStatement {
    private int line;
    private ExecutableStatement left;
    private Operator operator;
    private ExecutableStatement right;

    public LogicalOperatorStatement(int line, ExecutableStatement left, Operator operator, ExecutableStatement right) {
        this.line = line;
        this.left = left;
        this.operator = operator;
        this.right = right;
    }

    @Override
    public Execution createExecution() {
        return new LogicalOperatorExecution(line, left, operator, right);
    }

    @Override
    public boolean requiresSemicolon() {
        return false;
    }
}
