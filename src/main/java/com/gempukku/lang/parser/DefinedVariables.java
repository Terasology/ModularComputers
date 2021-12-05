// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package com.gempukku.lang.parser;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

public class DefinedVariables {
    private LinkedList<Set<String>> variablesStack = new LinkedList<Set<String>>();

    public DefinedVariables() {
        variablesStack.add(new HashSet<String>());
    }

    public void addDefinedVariable(String variableName) {
        variablesStack.getFirst().add(variableName);
    }

    public boolean isVariableDefined(String variableName) {
        for (Set<String> strings : variablesStack) {
            if (strings.contains(variableName)) {
                return true;
            }
        }
        return false;
    }

    public boolean isVariableDefinedInSameScope(String variableName) {
        return variablesStack.getFirst().contains(variableName);
    }

    public void pushNewContext() {
        variablesStack.addFirst(new HashSet<String>());
    }

    public void popContext() {
        variablesStack.removeFirst();
    }
}
