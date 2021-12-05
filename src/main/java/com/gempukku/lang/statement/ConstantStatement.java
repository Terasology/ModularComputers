// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package com.gempukku.lang.statement;

import com.gempukku.lang.ExecutableStatement;
import com.gempukku.lang.Execution;
import com.gempukku.lang.ExecutionContext;
import com.gempukku.lang.ExecutionCostConfiguration;
import com.gempukku.lang.ExecutionProgress;
import com.gempukku.lang.Variable;
import com.gempukku.lang.execution.SimpleExecution;

public class ConstantStatement implements ExecutableStatement {
    private Variable value;

    public ConstantStatement(Variable value) {
        this.value = value;
    }

    public Execution createExecution() {
        return new SimpleExecution() {
            @Override
            protected ExecutionProgress execute(ExecutionContext context, ExecutionCostConfiguration configuration) {
                context.setContextValue(value);
                return new ExecutionProgress(configuration.getSetContextValue());
            }
        };
    }

    @Override
    public boolean requiresSemicolon() {
        return true;
    }
}
