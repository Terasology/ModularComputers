// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package com.gempukku.lang.statement;

import com.gempukku.lang.ExecutableStatement;
import com.gempukku.lang.Execution;
import com.gempukku.lang.execution.AssignExecution;

public class AssignStatement implements ExecutableStatement {
    private ExecutableStatement name;
    private ExecutableStatement value;

    public AssignStatement(ExecutableStatement name, ExecutableStatement value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public Execution createExecution() {
        return new AssignExecution(name, value);
    }

    @Override
    public boolean requiresSemicolon() {
        return true;
    }
}
