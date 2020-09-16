// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.statement;

import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.DefiningExecutableStatement;
import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.ExecutableStatement;
import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.Execution;
import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.IllegalSyntaxException;
import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.execution.DefineAndAssignExecution;

public class DefineAndAssignStatement implements DefiningExecutableStatement {
    private final String _name;
    private final ExecutableStatement _value;

    public DefineAndAssignStatement(String name, ExecutableStatement value) throws IllegalSyntaxException {
        _name = name;
        _value = value;
    }

    @Override
    public String getDefinedVariableName() {
        return _name;
    }

    @Override
    public Execution createExecution() {
        return new DefineAndAssignExecution(_name, _value);
    }

    @Override
    public boolean requiresSemicolon() {
        return true;
    }
}
