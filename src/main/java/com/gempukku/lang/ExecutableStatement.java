package com.gempukku.lang;

public interface ExecutableStatement {
    public Execution createExecution();

    public boolean requiresSemicolon();
}
