// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.computer.system.common;

import com.gempukku.lang.ObjectDefinition;

import java.util.Collection;

public interface DocumentedObjectDefinition extends ObjectDefinition {
    Collection<String> getMethodNames();

    DocumentedFunctionExecutable getMethod(String methodName);
}
