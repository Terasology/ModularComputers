// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.modularcomputers.system.common;

import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.ObjectDefinition;

import java.util.Collection;

public interface DocumentedObjectDefinition extends ObjectDefinition {
    Collection<String> getMethodNames();

    DocumentedFunctionExecutable getMethod(String methodName);
}
