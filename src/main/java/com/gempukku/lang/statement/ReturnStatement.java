package com.gempukku.lang.statement;

import com.gempukku.lang.ExecutableStatement;
import com.gempukku.lang.Execution;
import com.gempukku.lang.execution.ReturnExecution;

public class ReturnStatement implements ExecutableStatement {
    private ExecutableStatement _result;

    public ReturnStatement(ExecutableStatement result) {
        _result = result;
    }

    @Override
    public Execution createExecution() {
        return new ReturnExecution(_result);
    }

    @Override
    public boolean requiresSemicolon() {
        return true;
    }
}
