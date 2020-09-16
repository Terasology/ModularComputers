// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.statement;

import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.ExecutableStatement;
import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.Execution;
import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.IllegalSyntaxException;
import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.execution.MemberAccessExecution;

public class MemberAccessStatement implements ExecutableStatement {
    private final int _line;
    private final ExecutableStatement _object;
    private final String _propertyName;

    public MemberAccessStatement(int line, ExecutableStatement object, String propertyName) throws IllegalSyntaxException {
        _line = line;
        _object = object;
        _propertyName = propertyName;
    }

    @Override
    public Execution createExecution() {
        return new MemberAccessExecution(_line, _object, _propertyName);
    }

    @Override
    public boolean requiresSemicolon() {
        return true;
    }
}
