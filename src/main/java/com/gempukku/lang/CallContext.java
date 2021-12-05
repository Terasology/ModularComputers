// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package com.gempukku.lang;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class CallContext {
    private CallContext parentContext;
    private boolean consumesReturn;
    private boolean consumesBreak;
    private Map<String, Variable> variables = new HashMap<String, Variable>();

    public CallContext(CallContext parentContext, boolean consumesBreak, boolean consumesReturn) {
        this.parentContext = parentContext;
        this.consumesBreak = consumesBreak;
        this.consumesReturn = consumesReturn;
    }

    public CallContext getParentContext() {
        return parentContext;
    }

    public boolean isConsumesBreak() {
        return consumesBreak;
    }

    public boolean isConsumesReturn() {
        return consumesReturn;
    }

    public Collection<Variable> getVariablesInContext() {
        return Collections.unmodifiableCollection(variables.values());
    }

    public Variable getVariableValue(String name) throws ExecutionException {
        final Variable variable = variables.get(name);
        if (variable != null) {
            return variable;
        } else if (parentContext != null) {
            return parentContext.getVariableValue(name);
        } else {
            throw new ExecutionException(-1, "Variable with this name is not defined in this scope: " + name);
        }
    }

    public Variable defineVariable(String name) throws ExecutionException {
        Variable variable = variables.get(name);
        if (variable != null) {
            throw new ExecutionException(-1, "Variable with this name is already defined in this scope: " + name);
        }
        variable = new Variable(null);
        variables.put(name, variable);
        return variable;
    }
}
