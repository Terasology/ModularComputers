// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.parser;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

public class DefinedVariables {
    private final LinkedList<Set<String>> _variablesStack = new LinkedList<Set<String>>();

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
