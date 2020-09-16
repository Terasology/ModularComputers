// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.statement;

import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.ExecutableStatement;
import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.Execution;
import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.execution.AssignExecution;

public class AssignStatement implements ExecutableStatement {
    private final ExecutableStatement _name;
    private final ExecutableStatement _value;

    public AssignStatement(ExecutableStatement name, ExecutableStatement value) {
        _name = name;
        _value = value;
    }

    @Override
    public Execution createExecution() {
        return new AssignExecution(_name, _value);
    }

    @Override
    public boolean requiresSemicolon() {
        return true;
    }
}
