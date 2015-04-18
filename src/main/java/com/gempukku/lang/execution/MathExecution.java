package com.gempukku.lang.execution;

import com.gempukku.lang.ExecutableStatement;
import com.gempukku.lang.Execution;
import com.gempukku.lang.ExecutionContext;
import com.gempukku.lang.ExecutionCostConfiguration;
import com.gempukku.lang.ExecutionException;
import com.gempukku.lang.ExecutionProgress;
import com.gempukku.lang.Operator;
import com.gempukku.lang.Variable;

public class MathExecution implements Execution {
    private int _line;
    private ExecutableStatement _left;
    private Operator _operator;
    private ExecutableStatement _right;
    private boolean _assignToLeft;

    private boolean _stackedLeft;
    private boolean _resolvedLeft;
    private boolean _stackedRight;
    private boolean _resolvedAndAssignedSum;

    private Variable _leftValue;

    public MathExecution(int line, ExecutableStatement left, Operator operator, ExecutableStatement right, boolean assignToLeft) {
        _line = line;
        _left = left;
        _operator = operator;
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
            if (rightValue.getType() == Variable.Type.NUMBER && _leftValue.getType() == Variable.Type.NUMBER) {
                final float valueLeft = ((Number) _leftValue.getValue()).floatValue();
                final float valueRight = ((Number) rightValue.getValue()).floatValue();
                Object result;
                if (_operator == Operator.SUBTRACT || _operator == Operator.SUBTRACT_ASSIGN)
                    result = valueLeft - valueRight;
                else if (_operator == Operator.DIVIDE || _operator == Operator.DIVIDE_ASSIGN)
                    result = valueLeft / valueRight;
                else if (_operator == Operator.MULTIPLY || _operator == Operator.MULTIPLY_ASSIGN)
                    result = valueLeft * valueRight;
                else if (_operator == Operator.MOD || _operator == Operator.MOD_ASSIGN)
                    result = valueLeft % valueRight;
                else if (_operator == Operator.GREATER_OR_EQUAL)
                    result = valueLeft >= valueRight;
                else if (_operator == Operator.GREATER)
                    result = valueLeft > valueRight;
                else if (_operator == Operator.LESS_OR_EQUAL)
                    result = valueLeft <= valueRight;
                else if (_operator == Operator.LESS)
                    result = valueLeft < valueRight;
                else
                    throw new ExecutionException(_line, "Unknown operator " + _operator);

                if (_assignToLeft)
                    _leftValue.setValue(result);
                executionContext.setContextValue(new Variable(result));
            } else {
                throw new ExecutionException(_line, "Unable to perform mathematical operation on two non-number values " + _leftValue.getType() + " and " + rightValue.getType());
            }
            _resolvedAndAssignedSum = true;
            return new ExecutionProgress(configuration.getGetContextValue() + configuration.getOtherMathOperation() + configuration.getSetContextValue());
        }
        return null;
    }

}
