// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.statement;

import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.ExecutableStatement;
import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.Execution;
import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.execution.MapAccessExecution;

public class MapAccessStatement implements ExecutableStatement {
    private final int _line;
    private final ExecutableStatement _mapStatement;
    private final ExecutableStatement _propertyStatement;

    public MapAccessStatement(int line, ExecutableStatement mapStatement, ExecutableStatement propertyStatement) {
        _line = line;
        _mapStatement = mapStatement;
        _propertyStatement = propertyStatement;
    }

    @Override
    public Execution createExecution() {
        return new MapAccessExecution(_line, _mapStatement, _propertyStatement);
    }

    @Override
    public boolean requiresSemicolon() {
        return false;
    }
}
