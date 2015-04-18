package com.gempukku.lang.parser;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

public class DefinedVariables {
    private LinkedList<Set<String>> _variablesStack = new LinkedList<Set<String>>();

    public DefinedVariables() {
        _variablesStack.add(new HashSet<String>());
    }

    public void addDefinedVariable(String variableName) {
        _variablesStack.getFirst().add(variableName);
    }

    public boolean isVariableDefined(String variableName) {
        for (Set<String> strings : _variablesStack) {
            if (strings.contains(variableName))
                return true;
        }
        return false;
    }

    public boolean isVariableDefinedInSameScope(String variableName) {
        return _variablesStack.getFirst().contains(variableName);
    }

    public void pushNewContext() {
        _variablesStack.addFirst(new HashSet<String>());
    }

    public void popContext() {
        _variablesStack.removeFirst();
    }
}
