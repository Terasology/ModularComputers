// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.statement;

import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.ExecutableStatement;
import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.Execution;
import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.execution.ListDefineExecution;

import java.util.List;

public class ListDefineStatement implements ExecutableStatement {
    private final List<ExecutableStatement> _values;

    public ListDefineStatement(List<ExecutableStatement> values) {
        _values = values;
    }

    @Override
    public boolean requiresSemicolon() {
        return false;
    }

    @Override
    public Execution createExecution() {
        return new ListDefineExecution(_values);
    }
}
