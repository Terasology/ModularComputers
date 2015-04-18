package com.gempukku.lang.statement;

import com.gempukku.lang.ExecutableStatement;
import com.gempukku.lang.Execution;
import com.gempukku.lang.execution.FunctionCallExecution;

import java.util.List;

public class FunctionCallStatement implements ExecutableStatement {
    private int _line;
    private ExecutableStatement _function;
    private List<ExecutableStatement> _parameters;

    public FunctionCallStatement(int line, ExecutableStatement function, List<ExecutableStatement> parameters) {
        _line = line;
        _function = function;
        _parameters = parameters;
    }

    @Override
    public Execution createExecution() {
        return new FunctionCallExecution(_line, _function, _parameters);
    }

    @Override
    public boolean requiresSemicolon() {
        return true;
    }
}
