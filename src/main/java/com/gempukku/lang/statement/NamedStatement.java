package com.gempukku.lang.statement;

import com.gempukku.lang.ExecutableStatement;
import com.gempukku.lang.Execution;

public class NamedStatement implements ExecutableStatement {
    private String _name;

    public NamedStatement(String name) {
        _name = name;
    }

    public String getName() {
        return _name;
    }

    @Override
    public Execution createExecution() {
        return null;
    }

    @Override
    public boolean requiresSemicolon() {
        return false;
    }
}
