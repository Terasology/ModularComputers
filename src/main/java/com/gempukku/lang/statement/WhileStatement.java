package com.gempukku.lang.statement;

import com.gempukku.lang.*;
import com.gempukku.lang.execution.SimpleExecution;
import com.gempukku.lang.execution.WhileExecution;

public class WhileStatement implements ExecutableStatement {
    private int _line;
    private ExecutableStatement _condition;
    private ExecutableStatement _statement;

    public WhileStatement(int line, ExecutableStatement condition, ExecutableStatement statement) {
        _line = line;
        _condition = condition;
        _statement = statement;
    }

    @Override
    public Execution createExecution() {
        return new SimpleExecution() {
            @Override
            protected ExecutionProgress execute(ExecutionContext context, ExecutionCostConfiguration configuration) throws ExecutionException {
                CallContext whileContext = new CallContext(context.peekCallContext(), true, false);
                context.stackExecutionGroup(whileContext,
                        new WhileExecution(_line, _condition, _statement));
                return new ExecutionProgress(configuration.getStackGroupExecution());
            }
        };
    }

    @Override
    public boolean requiresSemicolon() {
        return false;
    }
}
