// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.statement;

import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.ExecutableStatement;
import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.Execution;
import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.execution.ReturnExecution;

public class ReturnStatement implements ExecutableStatement {
    private final ExecutableStatement _result;

    public ReturnStatement(ExecutableStatement result) {
        _result = result;
    }

    @Override
    public Execution createExecution() {
        return new ReturnExecution(_result);
    }

    @Override
    public boolean requiresSemicolon() {
        return true;
    }
}
