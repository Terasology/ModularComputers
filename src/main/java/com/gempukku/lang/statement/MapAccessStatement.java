// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package com.gempukku.lang.statement;

import com.gempukku.lang.ExecutableStatement;
import com.gempukku.lang.Execution;
import com.gempukku.lang.execution.MapAccessExecution;

public class MapAccessStatement implements ExecutableStatement {
    private int line;
    private ExecutableStatement mapStatement;
    private ExecutableStatement propertyStatement;

    public MapAccessStatement(int line, ExecutableStatement mapStatement, ExecutableStatement propertyStatement) {
        this.line = line;
        this.mapStatement = mapStatement;
        this.propertyStatement = propertyStatement;
    }

    @Override
    public Execution createExecution() {
        return new MapAccessExecution(line, mapStatement, propertyStatement);
    }

    @Override
    public boolean requiresSemicolon() {
        return false;
    }
}
