package com.gempukku.lang.statement;

import com.gempukku.lang.*;
import com.gempukku.lang.execution.SimpleExecution;

public class DefineStatement implements DefiningExecutableStatement {
    private String _name;

    public DefineStatement(String name) throws IllegalSyntaxException {
        _name = name;
    }

    @Override
    public String getDefinedVariableName() {
        return _name;
    }

    @Override
    public Execution createExecution() {
        return new SimpleExecution() {
            @Override
            protected ExecutionProgress execute(ExecutionContext context, ExecutionCostConfiguration configuration) throws ExecutionException {
                context.peekCallContext().defineVariable(_name);
                return new ExecutionProgress(configuration.getDefineVariable());
            }
        };
    }

    @Override
    public boolean requiresSemicolon() {
        return true;
    }
}
