// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.modularcomputers.shadedlibs.com.gempukku.lang;

public class ExecutionProgress {
    private final int _cost;
    private final int _minExecutionTime;

    public ExecutionProgress(int cost) {
        this(cost, 0);
    }

    public ExecutionProgress(int cost, int minExecutionTime) {
        _cost = cost;
        _minExecutionTime = minExecutionTime;
    }

    public int getCost() {
        return _cost;
    }

    public int getMinExecutionTime() {
        return _minExecutionTime;
    }
}
