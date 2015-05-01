package com.gempukku.lang;

import java.util.Collection;

public class DefaultFunctionExecutable implements FunctionExecutable {
    private CallContext _callContext;
    private ExecutableStatement _statement;
    private Collection<String> _parameterNames;

    public DefaultFunctionExecutable(CallContext callContext, Collection<String> parameterNames) {
        _callContext = callContext;
        _parameterNames = parameterNames;
    }

    public void setStatement(ExecutableStatement statement) {
        _statement = statement;
    }

    @Override
    public Collection<String> getParameterNames() {
        return _parameterNames;
    }

    @Override
    public CallContext getCallContext() {
        return _callContext;
    }

    @Override
    public Execution createExecution(int line, ExecutionContext executionContext, CallContext context) {
        return _statement.createExecution();
    }
}
