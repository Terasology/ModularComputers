// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.statement;

import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.ExecutableStatement;
import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.Execution;
import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.IllegalSyntaxException;
import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.execution.MapDefineExecution;

import java.util.LinkedHashMap;
import java.util.Map;

public class MapDefineStatement implements ExecutableStatement {
    private final Map<String, ExecutableStatement> _properties = new LinkedHashMap<String, ExecutableStatement>();

    public MapDefineStatement() {

    }

    public void addProperty(int line, int column, String name, ExecutableStatement statement) throws IllegalSyntaxException {
        if (_properties.containsKey(name))
            throw new IllegalSyntaxException(line, column, "This map already contains an entry for this name");

        _properties.put(name, statement);
    }

    @Override
    public Execution createExecution() {
        return new MapDefineExecution(_properties);
    }

    @Override
    public boolean requiresSemicolon() {
        return false;
    }
}
