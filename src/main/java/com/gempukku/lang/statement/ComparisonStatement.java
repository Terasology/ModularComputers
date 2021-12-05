// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package com.gempukku.lang.statement;

import com.gempukku.lang.ExecutableStatement;
import com.gempukku.lang.Execution;
import com.gempukku.lang.Operator;
import com.gempukku.lang.execution.ComparisonExecution;

public class ComparisonStatement implements ExecutableStatement {
    private ExecutableStatement left;
    private Operator operator;
    private ExecutableStatement right;

    public ComparisonStatement(ExecutableStatement left, Operator operator, ExecutableStatement right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }

    @Override
    public Execution createExecution() {
        return new ComparisonExecution(left, operator, right);
    }

    @Override
    public boolean requiresSemicolon() {
        return false;
    }
}
