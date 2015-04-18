package com.gempukku.lang.execution;

import com.gempukku.lang.*;

import java.util.ArrayList;
import java.util.List;

public class ListDefineExecution implements Execution {
    private List<ExecutableStatement> _executableStatements;
    private int _nextStackIndex;
    private int _nextRetrieveIndex;

    private boolean _assignedResult;

    private List<Variable> _result = new ArrayList<Variable>();

    public ListDefineExecution(List<ExecutableStatement> executableStatements) {
        _executableStatements = executableStatements;
    }

    @Override
    public boolean hasNextExecution(ExecutionContext executionContext) {
        return !_assignedResult;
    }

    @Override
    public ExecutionProgress executeNextStatement(ExecutionContext executionContext, ExecutionCostConfiguration configuration) throws ExecutionException {
        if (_nextRetrieveIndex < _nextStackIndex) {
            _result.add(new Variable(executionContext.getContextValue().getValue()));
            _nextRetrieveIndex++;
            return new ExecutionProgress(configuration.getGetContextValue());
        }
        if (_nextStackIndex < _executableStatements.size()) {
            executionContext.stackExecution(_executableStatements.get(_nextStackIndex).createExecution());
            _nextStackIndex++;
            return new ExecutionProgress(configuration.getStackExecution());
        }
        if (!_assignedResult) {
            executionContext.setContextValue(new Variable(_result));
            _assignedResult = true;
            return new ExecutionProgress(configuration.getSetContextValue());
        }
        return null;
    }
}
