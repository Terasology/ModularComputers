package com.gempukku.lang.statement;

import com.gempukku.lang.ExecutableStatement;
import com.gempukku.lang.Execution;
import com.gempukku.lang.IllegalSyntaxException;
import com.gempukku.lang.execution.MemberAccessExecution;

public class MemberAccessStatement implements ExecutableStatement {
    private int _line;
    private ExecutableStatement _object;
    private String _propertyName;

    public MemberAccessStatement(int line, ExecutableStatement object, String propertyName) throws IllegalSyntaxException {
        _line = line;
        _object = object;
        _propertyName = propertyName;
    }

    @Override
    public Execution createExecution() {
        return new MemberAccessExecution(_line, _object, _propertyName);
    }

    @Override
    public boolean requiresSemicolon() {
        return true;
    }
}
