// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.statement;

import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.ExecutableStatement;
import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.Execution;

public class NamedStatement implements ExecutableStatement {
    private final String _name;

    public NamedStatement(String name) {
        _name = name;
    }

    public String getName() {
        return _name;
    }

    @Override
    public Execution createExecution() {
        return null;
    }

    @Override
    public boolean requiresSemicolon() {
        return false;
    }
}
