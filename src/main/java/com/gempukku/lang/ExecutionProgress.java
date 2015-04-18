package com.gempukku.lang;

public class ExecutionProgress {
    private int _cost;
    private int _minExecutionTicks;

    public ExecutionProgress(int cost) {
        this(cost, 0);
    }

    public ExecutionProgress(int cost, int minExecutionTicks) {
        _cost = cost;
        _minExecutionTicks = minExecutionTicks;
    }

    public int getCost() {
        return _cost;
    }

    public int getMinExecutionTicks() {
        return _minExecutionTicks;
    }
}
