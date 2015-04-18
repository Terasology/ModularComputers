package com.gempukku.lang.statement;

import com.gempukku.lang.*;
import com.gempukku.lang.execution.SimpleExecution;

public class VariableStatement implements ExecutableStatement {
    private String _name;

    public VariableStatement(String name) throws IllegalSyntaxException {
        _name = name;
    }

    public Execution createExecution() {
        return new SimpleExecution() {
            @Override
            protected ExecutionProgress execute(ExecutionContext context, ExecutionCostConfiguration configuration) throws ExecutionException {
                context.setContextValue(context.peekCallContext().getVariableValue(_name));
                return new ExecutionProgress(configuration.getSetContextValue());
            }
        };
    }

    public String getName() {
        return _name;
    }

    @Override
    public boolean requiresSemicolon() {
        return true;
    }
}
