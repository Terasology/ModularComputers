package com.gempukku.lang.statement;

import com.gempukku.lang.*;
import com.gempukku.lang.execution.MultiStatementExecution;

import java.util.List;

public class BlockStatement implements ExecutableStatement {
    private List<ExecutableStatement> _statements;
    private boolean _consumesBreak;
    private boolean _consumesReturn;

    public BlockStatement(List<ExecutableStatement> statements, boolean consumesBreak, boolean consumesReturn) {
        _statements = statements;
        _consumesBreak = consumesBreak;
        _consumesReturn = consumesReturn;
    }

    public Execution createExecution() {
        return new Execution() {
            private boolean _stacked;

            @Override
            public boolean hasNextExecution(ExecutionContext executionContext) {
                return !_stacked;
            }

            @Override
            public ExecutionProgress executeNextStatement(ExecutionContext executionContext, ExecutionCostConfiguration configuration) throws ExecutionException {
                CallContext blockContext = new CallContext(executionContext.peekCallContext(), _consumesBreak, _consumesReturn);
                executionContext.stackExecutionGroup(blockContext, new MultiStatementExecution(_statements));
                _stacked = true;
                return new ExecutionProgress(configuration.getStackGroupExecution());
            }
        };
    }

    @Override
    public boolean requiresSemicolon() {
        return true;
    }
}
