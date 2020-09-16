// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.modularcomputers.shadedlibs.com.gempukku.lang;

public class ScriptExecutable {
    private ExecutableStatement _statement;

    public void setStatement(ExecutableStatement statement) {
        _statement = statement;
    }

    public Execution createExecution(CallContext context) {
        return _statement.createExecution();
    }
}
