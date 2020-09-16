// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.modularcomputers.context;

import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.ExecutionContext;
import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.ExecutionCostConfiguration;

public class TerasologyComputerExecutionContext extends ExecutionContext {
    private final ComputerCallback computerCallback;

    public TerasologyComputerExecutionContext(ExecutionCostConfiguration configuration,
                                              ComputerCallback computerCallback) {
        super(configuration);
        this.computerCallback = computerCallback;
    }

    public ComputerCallback getComputerCallback() {
        return computerCallback;
    }
}
