// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package com.gempukku.lang;

import java.util.Collection;

public class DefaultFunctionExecutable implements FunctionExecutable {
    private CallContext callContext;
    private ExecutableStatement statement;
    private Collection<String> parameterNames;

    public DefaultFunctionExecutable(CallContext callContext, Collection<String> parameterNames) {
        this.callContext = callContext;
        this.parameterNames = parameterNames;
    }

    public void setStatement(ExecutableStatement statement) {
        this.statement = statement;
    }

    @Override
    public Collection<String> getParameterNames() {
        return parameterNames;
    }

    @Override
    public CallContext getCallContext() {
        return callContext;
    }

    @Override
    public Execution createExecution(int line, ExecutionContext executionContext, CallContext context) {
        return statement.createExecution();
    }
}
