package com.gempukku.lang;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ExecutionContext {
    private LinkedList<LinkedList<Execution>> _executionGroups = new LinkedList<LinkedList<Execution>>();
    private Variable _contextValue;
    private Variable _returnValue;

    private boolean _returnFromFunction;
    private boolean _breakFromBlock;

    private LinkedList<CallContext> _groupCallContexts = new LinkedList<CallContext>();
    private Map<Variable.Type, PropertyProducer> _perTypeProperties = new HashMap<Variable.Type, PropertyProducer>();

    private int _stackTraceSize = 0;
    private boolean _suspended;
    private ExecutionCostConfiguration _configuration;

    public ExecutionContext(ExecutionCostConfiguration configuration) {
        _configuration = configuration;
    }

    public int getStackTraceSize() {
        return _stackTraceSize;
    }

    public int getMemoryUsage() {
        Set<Object> counted = new HashSet<Object>();
        int result = 0;
        for (CallContext groupCallContext : _groupCallContexts)
            result += getVariablesSize(counted, groupCallContext.getVariablesInContext());
        return result;
    }

    private int sizeOf(Set<Object> counted, Object value) {
        if (value == null) {
            return 1;
        } else if (value instanceof String) {
            return ((String) value).length();
        } else if (value instanceof Number) {
            return 4;
        } else if (value instanceof Map) {
            Map<String, Variable> map = (Map<String, Variable>) value;
            return 4 + getVariablesSize(counted, map.values());
        } else if (value instanceof List) {
            List<Variable> list = (List<Variable>) value;
            return 4+ getVariablesSize(counted, list);
        } else if (value instanceof Boolean) {
            return 1;
        } else if (value instanceof FunctionExecutable) {
            CallContext functionContext = ((FunctionExecutable) value).getCallContext();
            return 4 + getVariablesSize(counted, functionContext.getVariablesInContext());
        } else if (value instanceof ObjectDefinition) {
            return 4;
        } else if (value instanceof CustomObject) {
            return ((CustomObject) value).sizeOf();
        } else if (value.getClass().isArray()) {
            return 4;
        } else
            throw new UnsupportedOperationException("Unknown type of variable value: " + value.getClass().getSimpleName());
    }

    private int getVariablesSize(Set<Object> counted, Collection<Variable> variables) {
        int result = 4;
        for (Variable variable : variables) {
            Object mapValue = variable.getValue();
            if (!counted.contains(mapValue)) {
                counted.add(mapValue);
                result += sizeOf(counted, mapValue);
            }
        }
        return result;
    }

    public void stackExecution(Execution execution) {
        _executionGroups.getLast().add(execution);
    }

    public ExecutionProgress executeNext() throws ExecutionException {
        while (!_executionGroups.isEmpty()) {
            final LinkedList<Execution> inBlockExecutionStack = _executionGroups.getLast();
            while (!inBlockExecutionStack.isEmpty()) {
                final Execution execution = inBlockExecutionStack.getLast();
                if (execution.hasNextExecution(this)) {
                    final ExecutionProgress executionProgress = execution.executeNextStatement(this, _configuration);
                    if (_breakFromBlock)
                        doTheBreak();
                    if (_returnFromFunction)
                        doTheReturn();
                    return executionProgress;
                } else
                    inBlockExecutionStack.removeLast();
            }
            _executionGroups.removeLast();
            removeLastCallContext();
        }
        return new ExecutionProgress(0);
    }

    private CallContext removeLastCallContext() {
        final CallContext removedCallContext = _groupCallContexts.removeLast();
        if (removedCallContext.isConsumesReturn())
            _stackTraceSize--;
        return removedCallContext;
    }

    private void doTheBreak() throws ExecutionException {
        CallContext callContext;
        do {
            if (_groupCallContexts.isEmpty())
                throw new ExecutionException(-1, "Break invoked without a containing block");
            callContext = removeLastCallContext();
            _executionGroups.removeLast();
        } while (!callContext.isConsumesBreak());
        _breakFromBlock = false;
    }

    private void doTheReturn() {
        CallContext callContext;
        do {
            callContext = removeLastCallContext();
            _executionGroups.removeLast();
        } while (!callContext.isConsumesReturn());
    }

    public Variable getContextValue() {
        return _contextValue;
    }

    public void setContextValue(Variable contextValue) {
        _contextValue = contextValue;
    }

    public Variable getReturnValue() {
        return _returnValue;
    }

    public void setReturnValue(Variable returnValue) {
        _returnValue = returnValue;
        _returnFromFunction = true;
    }

    public void breakBlock() {
        _breakFromBlock = true;
    }

    public void resetReturnValue() {
        _returnValue = null;
        _returnFromFunction = false;
    }

    public CallContext peekCallContext() {
        return _groupCallContexts.getLast();
    }

    public void setVariableValue(Variable variable, Object value) throws ExecutionException {
        variable.setValue(value);
    }

    public void stackExecutionGroup(CallContext callContext, Execution execution) {
        _groupCallContexts.add(callContext);
        LinkedList<Execution> functionExecutionStack = new LinkedList<Execution>();
        functionExecutionStack.add(execution);
        _executionGroups.add(functionExecutionStack);
        if (callContext.isConsumesReturn())
            _stackTraceSize++;
    }

    public boolean isFinished() {
        return _executionGroups.isEmpty();
    }

    public void setSuspended(boolean suspended) {
        _suspended = suspended;
    }

    public boolean isSuspended() {
        return _suspended;
    }

    public void addPropertyProducer(Variable.Type type, PropertyProducer producer) {
        _perTypeProperties.put(type, producer);
    }

    public Variable resolveMember(Variable object, String property) throws ExecutionException {
        if (!_perTypeProperties.containsKey(object.getType()))
            return new Variable(null);

        return _perTypeProperties.get(object.getType()).exposePropertyFor(this, object, property);
    }
}
