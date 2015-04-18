package com.gempukku.lang.statement;

import com.gempukku.lang.*;
import com.gempukku.lang.execution.SimpleExecution;

public class ConstantStatement implements ExecutableStatement {
    private Variable _value;

    public ConstantStatement(Variable value) {
        _value = value;
    }

    public Execution createExecution() {
        return new SimpleExecution() {
            @Override
            protected ExecutionProgress execute(ExecutionContext context, ExecutionCostConfiguration configuration) {
                context.setContextValue(_value);
                return new ExecutionProgress(configuration.getSetContextValue());
            }
        };
    }

    @Override
    public boolean requiresSemicolon() {
        return true;
    }
}
