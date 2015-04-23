package com.gempukku.lang;

public class ExecutionProgress {
    private int _cost;
    private int _minExecutionTime;

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
