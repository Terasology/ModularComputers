// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package com.gempukku.lang;

import java.util.Collection;

public interface FunctionExecutable {
    Collection<String> getParameterNames();

    CallContext getCallContext();

    Execution createExecution(int line, ExecutionContext executionContext, CallContext callContext);
}
