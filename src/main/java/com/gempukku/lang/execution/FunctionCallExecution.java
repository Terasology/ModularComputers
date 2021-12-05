// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package com.gempukku.lang.execution;

import com.gempukku.lang.CallContext;
import com.gempukku.lang.ExecutableStatement;
import com.gempukku.lang.Execution;
import com.gempukku.lang.ExecutionContext;
import com.gempukku.lang.ExecutionCostConfiguration;
import com.gempukku.lang.ExecutionException;
import com.gempukku.lang.ExecutionProgress;
import com.gempukku.lang.FunctionExecutable;
import com.gempukku.lang.Variable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class FunctionCallExecution implements Execution {
    private int line;
    private ExecutableStatement function;
    private List<ExecutableStatement> parameters;

    private boolean functionStacked;
    private boolean functionResolved;
    private int nextParameterIndexStacked;
    private int nextParameterValueStored;
    private boolean functionCalled;
    private boolean returnResultRead;

    private Variable functionVar;
    private List<Variable> parameterValues = new ArrayList<Variable>();

    public FunctionCallExecution(int line, ExecutableStatement function, List<ExecutableStatement> parameters) {
        this.line = line;
        this.function = function;
        this.parameters = parameters;
    }

    @Override
    public boolean hasNextExecution(ExecutionContext executionContext) {
        if (!functionStacked) {
            return true;
        }
        if (!functionResolved) {
            return true;
        }
        if (nextParameterValueStored < nextParameterIndexStacked) {
            return true;
        }
        if (nextParameterIndexStacked < parameters.size()) {
            return true;
        }
        if (!functionCalled) {
            return true;
        }
        return !returnResultRead;
    }

    @Override
    public ExecutionProgress executeNextStatement(ExecutionContext executionContext, ExecutionCostConfiguration configuration)
            throws ExecutionException {
        if (!functionStacked) {
            executionContext.stackExecution(function.createExecution());
            functionStacked = true;
            return new ExecutionProgress(configuration.getStackExecution());
        }
        if (!functionResolved) {
            functionVar = executionContext.getContextValue();
            functionResolved = true;
            return new ExecutionProgress(configuration.getGetContextValue());
        }
        if (nextParameterValueStored < nextParameterIndexStacked) {
            parameterValues.add(executionContext.getContextValue());
            nextParameterValueStored++;
            return new ExecutionProgress(configuration.getGetContextValue());
        }
        if (nextParameterIndexStacked < parameters.size()) {
            executionContext.stackExecution(parameters.get(nextParameterIndexStacked).createExecution());
            nextParameterIndexStacked++;
            return new ExecutionProgress(configuration.getStackExecution());
        }
        if (!functionCalled) {
            if (functionVar.getType() != Variable.Type.FUNCTION) {
                throw new ExecutionException(line, "Expected function");
            }
            FunctionExecutable func = (FunctionExecutable) functionVar.getValue();
            final CallContext functionContextParent = func.getCallContext();
            final Collection<String> parameterNames = func.getParameterNames();
            if (parameterValues.size() > parameterNames.size()) {
                throw new ExecutionException(line, "Function does not accept as many parameters");
            }

            CallContext functionContext = new CallContext(functionContextParent, false, true);
            int i = 0;
            for (String parameterName : parameterNames) {
                Variable var = functionContext.defineVariable(parameterName);
                if (i < parameterValues.size()) {
                    executionContext.setVariableValue(var, parameterValues.get(i).getValue());
                }
                i++;
            }
            executionContext.stackExecutionGroup(functionContext, func.createExecution(line, executionContext, functionContext));
            functionCalled = true;
            return new ExecutionProgress(configuration.getStackGroupExecution() + configuration.getSetVariable() * parameterValues.size());
        }
        if (!returnResultRead) {
            final Variable returnValue = executionContext.getReturnValue();
            executionContext.setContextValue(returnValue);
            executionContext.resetReturnValue();
            returnResultRead = true;
            return new ExecutionProgress(configuration.getGetReturnValue() + configuration.getSetContextValue());
        }
        return null;
    }
}
