package com.gempukku.lang.statement;

import com.gempukku.lang.ExecutableStatement;
import com.gempukku.lang.Execution;
import com.gempukku.lang.Operator;
import com.gempukku.lang.execution.LogicalOperatorExecution;

public class LogicalOperatorStatement implements ExecutableStatement {
    private int _line;
    private ExecutableStatement _left;
    private Operator _operator;
    private ExecutableStatement _right;

    public LogicalOperatorStatement(int line, ExecutableStatement left, Operator operator, ExecutableStatement right) {
        _line = line;
        _left = left;
        _operator = operator;
        _right = right;
    }

    @Override
    public Execution createExecution() {
        return new LogicalOperatorExecution(_line, _left, _operator, _right);
    }

    @Override
    public boolean requiresSemicolon() {
        return false;
    }
}
