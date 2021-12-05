// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package com.gempukku.lang.statement;

import com.gempukku.lang.ExecutableStatement;
import com.gempukku.lang.Execution;
import com.gempukku.lang.execution.ReturnExecution;

public class ReturnStatement implements ExecutableStatement {
    private ExecutableStatement result;

    public ReturnStatement(ExecutableStatement result) {
        this.result = result;
    }

    @Override
    public Execution createExecution() {
        return new ReturnExecution(result);
    }

    @Override
    public boolean requiresSemicolon() {
        return true;
    }
}
