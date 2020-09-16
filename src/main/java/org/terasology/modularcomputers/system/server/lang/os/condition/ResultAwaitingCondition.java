// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.modularcomputers.system.server.lang.os.condition;

import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.ExecutionException;
import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.Variable;

public interface ResultAwaitingCondition {
    boolean isMet() throws ExecutionException;

    Variable getReturnValue();

    void dispose();
}
