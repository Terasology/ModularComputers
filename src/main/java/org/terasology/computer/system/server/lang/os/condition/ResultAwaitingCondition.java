// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.computer.system.server.lang.os.condition;

import com.gempukku.lang.ExecutionException;
import com.gempukku.lang.Variable;

public interface ResultAwaitingCondition {
    boolean isMet() throws ExecutionException;

    Variable getReturnValue();

    void dispose();
}
