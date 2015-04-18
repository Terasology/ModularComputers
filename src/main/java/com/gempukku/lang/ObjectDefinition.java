package com.gempukku.lang;

public interface ObjectDefinition {
    public Variable getMember(ExecutionContext context, String name);
}
