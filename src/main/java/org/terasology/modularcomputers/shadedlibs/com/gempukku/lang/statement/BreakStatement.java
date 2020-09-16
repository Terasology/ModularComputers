// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.statement;

import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.ExecutableStatement;
import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.Execution;
import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.ExecutionContext;
import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.ExecutionCostConfiguration;
import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.ExecutionException;
import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.ExecutionProgress;
import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.execution.SimpleExecution;

public class BreakStatement implements ExecutableStatement {
    @Override
    public Execution createExecution() {
        return new SimpleExecution() {
            @Override
            protected ExecutionProgress execute(ExecutionContext context, ExecutionCostConfiguration configuration) throws ExecutionException {
                context.breakBlock();
                return new ExecutionProgress(configuration.getBreakBlock());
            }
        };
    }

    @Override
    public boolean requiresSemicolon() {
        return true;
    }
}
