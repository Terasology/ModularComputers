package com.gempukku.lang.statement;

import com.gempukku.lang.ExecutableStatement;
import com.gempukku.lang.Execution;
import com.gempukku.lang.execution.MapAccessExecution;

public class MapAccessStatement implements ExecutableStatement {
    private int _line;
    private ExecutableStatement _mapStatement;
    private ExecutableStatement _propertyStatement;

    public MapAccessStatement(int line, ExecutableStatement mapStatement, ExecutableStatement propertyStatement) {
        _line = line;
        _mapStatement = mapStatement;
        _propertyStatement = propertyStatement;
    }

    @Override
    public Execution createExecution() {
        return new MapAccessExecution(_line, _mapStatement, _propertyStatement);
    }

    @Override
    public boolean requiresSemicolon() {
        return false;
    }
}
