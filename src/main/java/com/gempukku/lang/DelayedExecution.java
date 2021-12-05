// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package com.gempukku.lang;

public class DelayedExecution implements Execution {
    private boolean delayed;
    private boolean startExecuted;
    private int delay;
    private int minExecutionTicks;
    private Execution execution;

    public DelayedExecution(int delay, int minExecutionTicks, Execution execution) {
        this.delay = delay;
        this.minExecutionTicks = minExecutionTicks;
        this.execution = execution;
    }

    @Override
    public boolean hasNextExecution(ExecutionContext executionContext) {
        return !delayed || execution.hasNextExecution(executionContext);
    }

    @Override
    public ExecutionProgress executeNextStatement(ExecutionContext executionContext, ExecutionCostConfiguration configuration)
            throws ExecutionException {
        if (!startExecuted) {
            startExecuted = true;
            onExecutionStart(executionContext);
        }
        if (!delayed) {
            delayed = true;
            return new ExecutionProgress(delay, minExecutionTicks);
        }
        return execution.executeNextStatement(executionContext, configuration);
    }

    protected void onExecutionStart(ExecutionContext executionContext) throws ExecutionException { }
}
