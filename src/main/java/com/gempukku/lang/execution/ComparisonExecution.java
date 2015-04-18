package com.gempukku.lang.execution;

import com.gempukku.lang.*;

public class ComparisonExecution implements Execution {
    private ExecutableStatement _left;
    private Operator _operator;
    private ExecutableStatement _right;

    private boolean _stackedLeft;
    private boolean _resolvedLeft;
    private boolean _stackedRight;
    private boolean _resolvedAndAssignedSum;

    private Variable _leftValue;

    public ComparisonExecution(ExecutableStatement left, Operator operator, ExecutableStatement right) {
        _left = left;
        _operator = operator;
        _right = right;
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
            if (_leftValue.getType() != rightValue.getType()) {
                executionContext.setContextValue(new Variable(_operator == Operator.NOT_EQUALS));
            } else {
                boolean equals;
                if (_leftValue.getType() == Variable.Type.STRING)
                    equals = _leftValue.getValue().equals(rightValue.getValue());
                else if (_leftValue.getType() == Variable.Type.NUMBER)
                    equals = ((Number) _leftValue.getValue()).floatValue() == ((Number) rightValue.getValue()).floatValue();
                else
                    equals = _leftValue.getValue() == rightValue.getValue();
                if (equals)
                    executionContext.setContextValue(new Variable(_operator == Operator.EQUALS));
                else
                    executionContext.setContextValue(new Variable(_operator == Operator.NOT_EQUALS));
            }
            _resolvedAndAssignedSum = true;
            return new ExecutionProgress(configuration.getGetContextValue() + configuration.getCompareValues() + configuration.getSetContextValue());
        }
        return null;
    }

}
