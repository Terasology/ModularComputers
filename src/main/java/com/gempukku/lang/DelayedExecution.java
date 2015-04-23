package com.gempukku.lang;

public class DelayedExecution implements Execution {
    private boolean _delayed;
    private boolean _startExecuted;
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
        if (!_startExecuted) {
            _startExecuted = true;
            onExecutionStart(executionContext);
        }
        if (!_delayed) {
            _delayed = true;
            return new ExecutionProgress(_delay, _minExecutionTicks);
        }
        return _execution.executeNextStatement(executionContext, configuration);
    }

    protected void onExecutionStart(ExecutionContext executionContext) throws ExecutionException { }
}
