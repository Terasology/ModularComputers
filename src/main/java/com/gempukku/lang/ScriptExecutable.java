package com.gempukku.lang;

public class ScriptExecutable {
    private ExecutableStatement _statement;

    public void setStatement(ExecutableStatement statement) {
        _statement = statement;
    }

    public Execution createExecution(CallContext context) {
        return _statement.createExecution();
    }
}
