// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package com.gempukku.lang;

public class ExecutionProgress {
    private int cost;
    private int minExecutionTime;

    public ExecutionProgress(int cost) {
        this(cost, 0);
    }

    public ExecutionProgress(int cost, int minExecutionTime) {
        this.cost = cost;
        this.minExecutionTime = minExecutionTime;
    }

    public int getCost() {
        return cost;
    }

    public int getMinExecutionTime() {
        return minExecutionTime;
    }
}
