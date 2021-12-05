// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package com.gempukku.lang.statement;

import com.gempukku.lang.ExecutableStatement;
import com.gempukku.lang.Execution;
import com.gempukku.lang.execution.FunctionCallExecution;

import java.util.List;

public class FunctionCallStatement implements ExecutableStatement {
    private int line;
    private ExecutableStatement function;
    private List<ExecutableStatement> parameters;

    public FunctionCallStatement(int line, ExecutableStatement function, List<ExecutableStatement> parameters) {
        this.line = line;
        this.function = function;
        this.parameters = parameters;
    }

    @Override
    public Execution createExecution() {
        return new FunctionCallExecution(line, function, parameters);
    }

    @Override
    public boolean requiresSemicolon() {
        return true;
    }
}
