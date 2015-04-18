package com.gempukku.lang;

public interface PropertyProducer {
    public Variable exposePropertyFor(ExecutionContext context, Variable object, String property) throws ExecutionException;
}
