// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.modularcomputers.system.common;

import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.ExecutionContext;
import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.Variable;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MapObjectDefinition implements DocumentedObjectDefinition {
    private final Map<String, Variable> variableMap = new HashMap<>();
    private final Map<String, DocumentedFunctionExecutable> methods = new HashMap<>();

    public void addMember(String name, DocumentedFunctionExecutable value) {
        variableMap.put(name, new Variable(value));
        methods.put(name, value);
    }

    @Override
    public Collection<String> getMethodNames() {
        return Collections.unmodifiableCollection(methods.keySet());
    }

    @Override
    public DocumentedFunctionExecutable getMethod(String methodName) {
        return methods.get(methodName);
    }

    @Override
    public Variable getMember(ExecutionContext context, String name) {
        Variable variable = variableMap.get(name);
        if (variable != null) {
            return variable;
        }
        return new Variable(null);
    }
}
