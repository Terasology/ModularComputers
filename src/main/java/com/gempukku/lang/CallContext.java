package com.gempukku.lang;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class CallContext {
    private CallContext _parentContext;
    private boolean _consumesReturn;
    private boolean _consumesBreak;
    private Map<String, Variable> _variables = new HashMap<String, Variable>();

    public CallContext(CallContext parentContext, boolean consumesBreak, boolean consumesReturn) {
        _parentContext = parentContext;
        _consumesBreak = consumesBreak;
        _consumesReturn = consumesReturn;
    }

    public CallContext getParentContext() {
        return _parentContext;
    }

    public boolean isConsumesBreak() {
        return _consumesBreak;
    }

    public boolean isConsumesReturn() {
        return _consumesReturn;
    }

    public Collection<Variable> getVariablesInContext() {
        return Collections.unmodifiableCollection(_variables.values());
    }

    public Variable getVariableValue(String name) throws ExecutionException {
        final Variable variable = _variables.get(name);
        if (variable != null)
            return variable;
        else if (_parentContext != null)
            return _parentContext.getVariableValue(name);
        else
            throw new ExecutionException(-1, "Variable with this name is not defined in this scope: " + name);
    }

    public Variable defineVariable(String name) throws ExecutionException {
        Variable variable = _variables.get(name);
        if (variable != null)
            throw new ExecutionException(-1, "Variable with this name is already defined in this scope: " + name);
        variable = new Variable(null);
        _variables.put(name, variable);
        return variable;
    }
}
