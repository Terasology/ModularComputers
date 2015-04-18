package com.gempukku.lang.execution;

import com.gempukku.lang.*;

public class DefineAndAssignExecution implements Execution {
    private String _name;
    private ExecutableStatement _value;

    private boolean _defined;
    private boolean _stackedValue;
    private boolean _assignedValue;

    private Variable _variable;

    public DefineAndAssignExecution(String name, ExecutableStatement value) {
        _name = name;
        _value = value;
    }

    @Override
    public boolean hasNextExecution(ExecutionContext executionContext) {
        if (!_defined)
            return true;
        if (!_stackedValue)
            return true;
        if (!_assignedValue)
            return true;
        return false;
    }

    @Override
    public ExecutionProgress executeNextStatement(ExecutionContext executionContext, ExecutionCostConfiguration configuration) throws ExecutionException {
        if (!_defined) {
            _variable = executionContext.peekCallContext().defineVariable(_name);
            _defined = true;
            return new ExecutionProgress(configuration.getDefineVariable());
        }
        if (!_stackedValue) {
            executionContext.stackExecution(_value.createExecution());
            _stackedValue = true;
            return new ExecutionProgress(configuration.getStackExecution());
        }
        if (!_assignedValue) {
            executionContext.setVariableValue(_variable, executionContext.getContextValue().getValue());
            _assignedValue = true;
            return new ExecutionProgress(configuration.getGetContextValue() + configuration.getSetVariable());
        }
        return null;
    }
}
