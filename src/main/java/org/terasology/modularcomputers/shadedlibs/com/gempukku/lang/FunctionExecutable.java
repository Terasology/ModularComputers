// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.modularcomputers.shadedlibs.com.gempukku.lang;

import java.util.Collection;

public interface FunctionExecutable {
    Collection<String> getParameterNames();

    CallContext getCallContext();

    Execution createExecution(int line, ExecutionContext executionContext, CallContext callContext);
}
