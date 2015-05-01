package com.gempukku.lang;

import java.util.Collection;

public interface FunctionExecutable {
    public Collection<String> getParameterNames();

    public CallContext getCallContext();

    public Execution createExecution(int line, ExecutionContext executionContext, CallContext callContext);
}
