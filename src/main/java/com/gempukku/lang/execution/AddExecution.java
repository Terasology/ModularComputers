package com.gempukku.lang.execution;

import com.gempukku.lang.*;

public class AddExecution implements Execution {
    private int _line;
    private ExecutableStatement _left;
    private ExecutableStatement _right;
    private boolean _assignToLeft;

    private boolean _stackedLeft;
    private boolean _resolvedLeft;
    private boolean _stackedRight;
    private boolean _resolvedAndAssignedSum;

    private Variable _leftValue;

    public AddExecution(int line, ExecutableStatement left, ExecutableStatement right, boolean assignToLeft) {
        _line = line;
        _left = left;
        _right = right;
        _assignToLeft = assignToLeft;
    }

    @Override
    public boolean hasNextExecution(ExecutionContext executionContext) {
        if (!_stackedLeft)
            return true;
        if (!_resolvedLeft)
            return true;
        if (!_stackedRight)
            return true;
        if (!_resolvedAndAssignedSum)
            return true;
        return false;
    }

    @Override
    public ExecutionProgress executeNextStatement(ExecutionContext executionContext, ExecutionCostConfiguration configuration) throws ExecutionException {
        if (!_stackedLeft) {
            executionContext.stackExecution(_left.createExecution());
            _stackedLeft = true;
            return new ExecutionProgress(configuration.getStackExecution());
        }
        if (!_resolvedLeft) {
            _leftValue = executionContext.getContextValue();
            _resolvedLeft = true;
            return new ExecutionProgress(configuration.getGetContextValue());
        }
        if (!_stackedRight) {
            executionContext.stackExecution(_right.createExecution());
            _stackedRight = true;
            return new ExecutionProgress(configuration.getStackExecution());
        }
        if (!_resolvedAndAssignedSum) {
            Variable rightValue = executionContext.getContextValue();
            Object result;
            if (_leftValue.getType() == Variable.Type.STRING) {
                result = convertToString(_leftValue) + convertToString(rightValue);
            } else if (rightValue.getType() == Variable.Type.NUMBER && _leftValue.getType() == Variable.Type.NUMBER) {
                result = ((Number) _leftValue.getValue()).floatValue() + ((Number) rightValue.getValue()).floatValue();
            } else {
                throw new ExecutionException(_line, "Unable to add two values of types " + _leftValue.getType() + " and " + rightValue.getType());
            }
            if (_assignToLeft)
                _leftValue.setValue(result);
            executionContext.setContextValue(new Variable(result));
            _resolvedAndAssignedSum = true;
            return new ExecutionProgress(configuration.getGetContextValue() + configuration.getSumValues());
        }
        return null;
    }

    private String convertToString(Variable variable) {
        if (variable.getType() == Variable.Type.STRING)
            return (String) variable.getValue();
        else if (variable.getType() == Variable.Type.NUMBER)
            return String.valueOf(((Number) variable.getValue()).floatValue());
        else if (variable.getType() == Variable.Type.NULL)
            return "null";
        else if (variable.getType() == Variable.Type.BOOLEAN)
            return ((Boolean) variable.getValue()) ? "true" : "false";
        else
            return "";
    }
}
