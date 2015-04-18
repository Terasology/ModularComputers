package com.gempukku.lang.statement;

import com.gempukku.lang.DefiningExecutableStatement;
import com.gempukku.lang.Execution;
import com.gempukku.lang.ExecutionContext;
import com.gempukku.lang.ExecutionCostConfiguration;
import com.gempukku.lang.ExecutionException;
import com.gempukku.lang.ExecutionProgress;
import com.gempukku.lang.IllegalSyntaxException;
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
