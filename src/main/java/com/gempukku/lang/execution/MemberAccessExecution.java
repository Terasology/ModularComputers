package com.gempukku.lang.execution;

import com.gempukku.lang.*;

public class MemberAccessExecution implements Execution {
    private int _line;
    private ExecutableStatement _object;
    private String _propertyName;

    private boolean _objectStacked;
    private boolean _objectResolved;

    private boolean _memberAccessStored;

    private Variable _objectValue;

    public MemberAccessExecution(int line, ExecutableStatement object, String propertyName) {
        _line = line;
        _object = object;
        _propertyName = propertyName;
    }

    @Override
    public boolean hasNextExecution(ExecutionContext executionContext) {
        if (!_objectStacked)
            return true;
        if (!_objectResolved)
            return true;
        if (!_memberAccessStored)
            return true;
        return false;
    }

    @Override
    public ExecutionProgress executeNextStatement(ExecutionContext executionContext, ExecutionCostConfiguration configuration) throws ExecutionException {
        if (!_objectStacked) {
            executionContext.stackExecution(_object.createExecution());
            _objectStacked = true;
            return new ExecutionProgress(configuration.getStackExecution());
        }
        if (!_objectResolved) {
            _objectValue = executionContext.getContextValue();
            _objectResolved = true;
            return new ExecutionProgress(configuration.getGetContextValue());
        }
        if (!_memberAccessStored) {
            final Variable member = executionContext.resolveMember(_objectValue, _propertyName);
            if (member == null)
                throw new ExecutionException(_line, "Property " + _propertyName + " not found");
            executionContext.setContextValue(member);
            _memberAccessStored = true;
            return new ExecutionProgress(configuration.getSetContextValue() + configuration.getResolveMember());
        }
        return null;
    }
}
