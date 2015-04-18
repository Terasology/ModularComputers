package com.gempukku.lang.execution;

import com.gempukku.lang.*;

import java.util.ArrayList;
import java.util.List;

public class FunctionCallExecution implements Execution {
    private int _line;
    private ExecutableStatement _function;
    private List<ExecutableStatement> _parameters;

    private boolean _functionStacked;
    private boolean _functionResolved;
    private int _nextParameterIndexStacked;
    private int _nextParameterValueStored;
    private boolean _functionCalled;
    private boolean _returnResultRead;

    private Variable _functionVar;
    private List<Variable> _parameterValues = new ArrayList<Variable>();

    public FunctionCallExecution(int line, ExecutableStatement function, List<ExecutableStatement> parameters) {
        _line = line;
        _function = function;
        _parameters = parameters;
    }

    @Override
    public boolean hasNextExecution(ExecutionContext executionContext) {
        if (!_functionStacked)
            return true;
        if (!_functionResolved)
            return true;
        if (_nextParameterValueStored < _nextParameterIndexStacked)
            return true;
        if (_nextParameterIndexStacked < _parameters.size())
            return true;
        if (!_functionCalled)
            return true;
        if (!_returnResultRead)
            return true;
        return false;
    }

    @Override
    public ExecutionProgress executeNextStatement(ExecutionContext executionContext, ExecutionCostConfiguration configuration) throws ExecutionException {
        if (!_functionStacked) {
            executionContext.stackExecution(_function.createExecution());
            _functionStacked = true;
            return new ExecutionProgress(configuration.getStackExecution());
        }
        if (!_functionResolved) {
            _functionVar = executionContext.getContextValue();
            _functionResolved = true;
            return new ExecutionProgress(configuration.getGetContextValue());
        }
        if (_nextParameterValueStored < _nextParameterIndexStacked) {
            _parameterValues.add(executionContext.getContextValue());
            _nextParameterValueStored++;
            return new ExecutionProgress(configuration.getGetContextValue());
        }
        if (_nextParameterIndexStacked < _parameters.size()) {
            executionContext.stackExecution(_parameters.get(_nextParameterIndexStacked).createExecution());
            _nextParameterIndexStacked++;
            return new ExecutionProgress(configuration.getStackExecution());
        }
        if (!_functionCalled) {
            if (_functionVar.getType() != Variable.Type.FUNCTION)
                throw new ExecutionException(_line, "Expected function");
            FunctionExecutable function = (FunctionExecutable) _functionVar.getValue();
            final CallContext functionContextParent = function.getCallContext();
            final String[] parameterNames = function.getParameterNames();
            if (_parameterValues.size() > parameterNames.length)
                throw new ExecutionException(_line, "Function does not accept as many parameters");

            CallContext functionContext = new CallContext(functionContextParent, false, true);
            for (int i = 0; i < parameterNames.length; i++) {
                Variable var = functionContext.defineVariable(parameterNames[i]);
                if (i < _parameterValues.size())
                    executionContext.setVariableValue(var, _parameterValues.get(i).getValue());
            }
            executionContext.stackExecutionGroup(functionContext, function.createExecution(_line, executionContext, functionContext));
            _functionCalled = true;
            return new ExecutionProgress(configuration.getStackGroupExecution() + configuration.getSetVariable() * _parameterValues.size());
        }
        if (!_returnResultRead) {
            final Variable returnValue = executionContext.getReturnValue();
            executionContext.setContextValue(returnValue);
            executionContext.resetReturnValue();
            _returnResultRead = true;
            return new ExecutionProgress(configuration.getGetReturnValue() + configuration.getSetContextValue());
        }
        return null;
    }
}
