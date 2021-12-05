// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package com.gempukku.lang;

public class ScriptExecutable {
    private ExecutableStatement statement;

    public void setStatement(ExecutableStatement statement) {
        this.statement = statement;
    }

    public Execution createExecution(CallContext context) {
        return statement.createExecution();
    }
}
