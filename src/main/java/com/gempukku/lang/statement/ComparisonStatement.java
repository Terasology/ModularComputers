package com.gempukku.lang.statement;

import com.gempukku.lang.ExecutableStatement;
import com.gempukku.lang.Execution;
import com.gempukku.lang.Operator;
import com.gempukku.lang.execution.ComparisonExecution;

public class ComparisonStatement implements ExecutableStatement {
    private ExecutableStatement _left;
    private Operator _operator;
    private ExecutableStatement _right;

    public ComparisonStatement(ExecutableStatement left, Operator operator, ExecutableStatement right) {
        _left = left;
        _operator = operator;
        _right = right;
    }

    @Override
    public Execution createExecution() {
        return new ComparisonExecution(_left, _operator, _right);
    }

    @Override
    public boolean requiresSemicolon() {
        return false;
    }
}
