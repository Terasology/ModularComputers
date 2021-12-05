// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package com.gempukku.lang.statement;

import com.gempukku.lang.ExecutableStatement;
import com.gempukku.lang.Execution;
import com.gempukku.lang.IllegalSyntaxException;
import com.gempukku.lang.execution.MemberAccessExecution;

public class MemberAccessStatement implements ExecutableStatement {
    private int line;
    private ExecutableStatement object;
    private String propertyName;

    public MemberAccessStatement(int line, ExecutableStatement object, String propertyName) throws IllegalSyntaxException {
        this.line = line;
        this.object = object;
        this.propertyName = propertyName;
    }

    @Override
    public Execution createExecution() {
        return new MemberAccessExecution(line, object, propertyName);
    }

    @Override
    public boolean requiresSemicolon() {
        return true;
    }
}
