// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package com.gempukku.lang.statement;

import com.gempukku.lang.ExecutableStatement;
import com.gempukku.lang.Execution;
import com.gempukku.lang.execution.AddExecution;

public class AddStatement implements ExecutableStatement {
    private int line;
    private ExecutableStatement left;
    private ExecutableStatement right;
    private boolean assignToLeft;

    public AddStatement(int line, ExecutableStatement left, ExecutableStatement right, boolean assignToLeft) {
        this.line = line;
        this.left = left;
        this.right = right;
        this.assignToLeft = assignToLeft;
    }

    @Override
    public Execution createExecution() {
        return new AddExecution(line, left, right, assignToLeft);
    }

    @Override
    public boolean requiresSemicolon() {
        return true;
    }
}
