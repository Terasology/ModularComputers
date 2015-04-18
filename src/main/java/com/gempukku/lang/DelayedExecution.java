package com.gempukku.lang;

public class DelayedExecution implements Execution {
    private boolean _delayed;
    private int _delay;
    private int _minExecutionTicks;
    private Execution _execution;

    public DelayedExecution(int delay, int minExecutionTicks, Execution execution) {
        _delay = delay;
        _minExecutionTicks = minExecutionTicks;
        _execution = execution;
    }

    @Override
    public boolean hasNextExecution(ExecutionContext executionContext) {
        return !_delayed || _execution.hasNextExecution(executionContext);
    }

    @Override
    public ExecutionProgress executeNextStatement(ExecutionContext executionContext, ExecutionCostConfiguration configuration) throws ExecutionException {
        if (!_delayed) {
            _delayed = true;
            return new ExecutionProgress(_delay, _minExecutionTicks);
        }
        return _execution.executeNextStatement(executionContext, configuration);
    }
}
