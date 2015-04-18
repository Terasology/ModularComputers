package com.gempukku.lang.statement;

import com.gempukku.lang.*;
import com.gempukku.lang.execution.ForExecution;
import com.gempukku.lang.execution.SimpleExecution;

public class ForStatement implements ExecutableStatement {
    private int _line;
    private ExecutableStatement _initializationStatement;
    private ExecutableStatement _terminationCondition;
    private ExecutableStatement _executedAfterEachLoop;
    private ExecutableStatement _statementInLoop;

    public ForStatement(int line, ExecutableStatement initializationStatement, ExecutableStatement terminationCondition, ExecutableStatement executedAfterEachLoop, ExecutableStatement statementInLoop) {
        _line = line;
        _initializationStatement = initializationStatement;
        _terminationCondition = terminationCondition;
        _executedAfterEachLoop = executedAfterEachLoop;
        _statementInLoop = statementInLoop;
    }

    @Override
    public Execution createExecution() {
        return new SimpleExecution() {
            @Override
            protected ExecutionProgress execute(ExecutionContext context, ExecutionCostConfiguration configuration) throws ExecutionException {
                CallContext forContext = new CallContext(context.peekCallContext(), true, false);
                context.stackExecutionGroup(forContext,
                        new ForExecution(_line, _initializationStatement, _terminationCondition, _executedAfterEachLoop, _statementInLoop));
                return new ExecutionProgress(configuration.getStackGroupExecution());
            }
        };
    }

    @Override
    public boolean requiresSemicolon() {
        return false;
    }
}
