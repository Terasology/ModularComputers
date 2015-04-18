package com.gempukku.lang.statement;

import com.gempukku.lang.ExecutableStatement;
import com.gempukku.lang.Execution;
import com.gempukku.lang.execution.IncrementDecrementExecution;

public class IncrementDecrementStatement implements ExecutableStatement {
    private int _line;
    private ExecutableStatement _expression;
    private boolean _increment;
    private boolean _pre;

    public IncrementDecrementStatement(int line, ExecutableStatement expression, boolean increment, boolean pre) {
        _line = line;
        _expression = expression;
        _increment = increment;
        _pre = pre;
    }

    @Override
    public boolean requiresSemicolon() {
        return false;
    }

    @Override
    public Execution createExecution() {
        return new IncrementDecrementExecution(_line, _expression, _increment, _pre);
    }
}
