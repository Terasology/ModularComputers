package com.gempukku.lang.statement;

import com.gempukku.lang.ExecutableStatement;
import com.gempukku.lang.Execution;
import com.gempukku.lang.execution.AssignExecution;

public class AssignStatement implements ExecutableStatement {
    private ExecutableStatement _name;
    private ExecutableStatement _value;

    public AssignStatement(ExecutableStatement name, ExecutableStatement value) {
        _name = name;
        _value = value;
    }

    @Override
    public Execution createExecution() {
        return new AssignExecution(_name, _value);
    }

    @Override
    public boolean requiresSemicolon() {
        return true;
    }
}
