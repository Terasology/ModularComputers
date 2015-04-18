package com.gempukku.lang;

public class DefaultFunctionExecutable implements FunctionExecutable {
    private CallContext _callContext;
    private ExecutableStatement _statement;
    private String[] _parameterNames;

    public DefaultFunctionExecutable(CallContext callContext, String[] parameterNames) {
        _callContext = callContext;
        _parameterNames = parameterNames;
    }

    public void setStatement(ExecutableStatement statement) {
        _statement = statement;
    }

    @Override
    public String[] getParameterNames() {
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
