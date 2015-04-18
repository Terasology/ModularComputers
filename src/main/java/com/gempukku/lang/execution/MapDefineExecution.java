package com.gempukku.lang.execution;

import com.gempukku.lang.ExecutableStatement;
import com.gempukku.lang.Execution;
import com.gempukku.lang.ExecutionContext;
import com.gempukku.lang.ExecutionCostConfiguration;
import com.gempukku.lang.ExecutionException;
import com.gempukku.lang.ExecutionProgress;
import com.gempukku.lang.Variable;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MapDefineExecution implements Execution {
    private Iterator<Map.Entry<String, ExecutableStatement>> _propertiesIterator;
    private String _lastKey;
    private boolean _hasToAssign;

    private boolean _finished;
    private Map<String, Variable> _result = new HashMap<String, Variable>();

    public MapDefineExecution(Map<String, ExecutableStatement> properties) {
        _propertiesIterator = properties.entrySet().iterator();
    }

    @Override
    public boolean hasNextExecution(ExecutionContext executionContext) {
        if (_finished)
            return false;

        return true;
    }

    @Override
    public ExecutionProgress executeNextStatement(ExecutionContext executionContext, ExecutionCostConfiguration configuration) throws ExecutionException {
        if (_hasToAssign) {
            _result.put(_lastKey, new Variable(executionContext.getContextValue().getValue()));
            _hasToAssign = false;
            return new ExecutionProgress(configuration.getGetContextValue());
        }
        if (_propertiesIterator.hasNext()) {
            final Map.Entry<String, ExecutableStatement> property = _propertiesIterator.next();
            _lastKey = property.getKey();
            _hasToAssign = true;
            executionContext.stackExecution(property.getValue().createExecution());
            return new ExecutionProgress(configuration.getStackExecution());
        }
        if (!_finished) {
            _finished = true;
            executionContext.setContextValue(new Variable(_result));
            return new ExecutionProgress(configuration.getSetContextValue());
        }
        return null;
    }
}
