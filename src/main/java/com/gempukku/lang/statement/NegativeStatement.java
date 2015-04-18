package com.gempukku.lang.statement;

import com.gempukku.lang.ExecutableStatement;
import com.gempukku.lang.Execution;
import com.gempukku.lang.execution.NegativeExecution;

public class NegativeStatement implements ExecutableStatement {
    private int _line;
    private ExecutableStatement _expression;

    public NegativeStatement(int line, ExecutableStatement expression) {
        _line = line;
        _expression = expression;
    }

    @Override
    public Execution createExecution() {
        return new NegativeExecution(_line, _expression);
    }

    @Override
    public boolean requiresSemicolon() {
        return false;
    }
}
