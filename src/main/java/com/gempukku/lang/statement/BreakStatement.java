package com.gempukku.lang.statement;

import com.gempukku.lang.*;
import com.gempukku.lang.execution.SimpleExecution;

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
