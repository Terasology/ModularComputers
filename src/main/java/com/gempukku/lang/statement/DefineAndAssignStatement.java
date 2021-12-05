// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package com.gempukku.lang.statement;

import com.gempukku.lang.DefiningExecutableStatement;
import com.gempukku.lang.ExecutableStatement;
import com.gempukku.lang.Execution;
import com.gempukku.lang.IllegalSyntaxException;
import com.gempukku.lang.execution.DefineAndAssignExecution;

public class DefineAndAssignStatement implements DefiningExecutableStatement {
    private String name;
    private ExecutableStatement value;

    public DefineAndAssignStatement(String name, ExecutableStatement value) throws IllegalSyntaxException {
        this.name = name;
        this.value = value;
    }

    @Override
    public String getDefinedVariableName() {
        return name;
    }

    @Override
    public Execution createExecution() {
        return new DefineAndAssignExecution(name, value);
    }

    @Override
    public boolean requiresSemicolon() {
        return true;
    }
}
