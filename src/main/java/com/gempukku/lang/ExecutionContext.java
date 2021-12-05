// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package com.gempukku.lang;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ExecutionContext {
    private LinkedList<LinkedList<Execution>> executionGroups = new LinkedList<LinkedList<Execution>>();
    private Variable contextValue;
    private Variable returnValue;

    private boolean returnFromFunction;
    private boolean breakFromBlock;

    private LinkedList<CallContext> groupCallContexts = new LinkedList<CallContext>();
    private Map<Variable.Type, PropertyProducer> perTypeProperties = new HashMap<Variable.Type, PropertyProducer>();

    private int stackTraceSize = 0;
    private boolean suspended;
    private ExecutionCostConfiguration configuration;

    public ExecutionContext(ExecutionCostConfiguration configuration) {
        this.configuration = configuration;
    }

    public int getStackTraceSize() {
        return stackTraceSize;
    }

    public int getMemoryUsage() {
        Set<Object> counted = new HashSet<Object>();
        int result = 0;
        for (CallContext groupCallContext : groupCallContexts) {
            result += getVariablesSize(counted, groupCallContext.getVariablesInContext());
        }
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
        } else
            throw new UnsupportedOperationException("Unknown type of variable value: " + value.getClass().getSimpleName());
    }

    private int getVariablesSize(Set<Object> counted, Collection<Variable> variables) {
        int result = 4;
        for (Variable variable : variables) {
            Object value = variable.getValue();
            if (!counted.contains(value)) {
                counted.add(value);
                result += sizeOf(counted, value);
            }
        }
        return result;
    }

    public void stackExecution(Execution execution) {
        executionGroups.getLast().add(execution);
    }

    public ExecutionProgress executeNext() throws ExecutionException {
        while (!executionGroups.isEmpty()) {
            final LinkedList<Execution> inBlockExecutionStack = executionGroups.getLast();
            while (!inBlockExecutionStack.isEmpty()) {
                final Execution execution = inBlockExecutionStack.getLast();
                if (execution.hasNextExecution(this)) {
                    final ExecutionProgress executionProgress = execution.executeNextStatement(this, configuration);
                    if (breakFromBlock) {
                        doTheBreak();
                    }
                    if (returnFromFunction) {
                        doTheReturn();
                    }
                    return executionProgress;
                } else {
                    inBlockExecutionStack.removeLast();
                }
            }
            executionGroups.removeLast();
            removeLastCallContext();
        }
        return new ExecutionProgress(0);
    }

    private CallContext removeLastCallContext() {
        final CallContext removedCallContext = groupCallContexts.removeLast();
        if (removedCallContext.isConsumesReturn()) {
            stackTraceSize--;
        }
        return removedCallContext;
    }

    private void doTheBreak() throws ExecutionException {
        CallContext callContext;
        do {
            if (groupCallContexts.isEmpty()) {
                throw new ExecutionException(-1, "Break invoked without a containing block");
            }
            callContext = removeLastCallContext();
            executionGroups.removeLast();
        } while (!callContext.isConsumesBreak());
        breakFromBlock = false;
    }

    private void doTheReturn() {
        CallContext callContext;
        do {
            callContext = removeLastCallContext();
            executionGroups.removeLast();
        } while (!callContext.isConsumesReturn());
    }

    public Variable getContextValue() {
        return contextValue;
    }

    public void setContextValue(Variable contextValue) {
        this.contextValue = contextValue;
    }

    public Variable getReturnValue() {
        return returnValue;
    }

    public void setReturnValue(Variable returnValue) {
        this.returnValue = returnValue;
        returnFromFunction = true;
    }

    public void breakBlock() {
        breakFromBlock = true;
    }

    public void resetReturnValue() {
        returnValue = null;
        returnFromFunction = false;
    }

    public CallContext peekCallContext() {
        return groupCallContexts.getLast();
    }

    public void setVariableValue(Variable variable, Object value) throws ExecutionException {
        variable.setValue(value);
    }

    public void stackExecutionGroup(CallContext callContext, Execution execution) {
        groupCallContexts.add(callContext);
        LinkedList<Execution> functionExecutionStack = new LinkedList<Execution>();
        functionExecutionStack.add(execution);
        executionGroups.add(functionExecutionStack);
        if (callContext.isConsumesReturn()) {
            stackTraceSize++;
        }
    }

    public boolean isFinished() {
        return executionGroups.isEmpty();
    }

    public void setSuspended(boolean suspended) {
        this.suspended = suspended;
    }

    public boolean isSuspended() {
        return suspended;
    }

    public void addPropertyProducer(Variable.Type type, PropertyProducer producer) {
        perTypeProperties.put(type, producer);
    }

    public Variable resolveMember(Variable object, String property) throws ExecutionException {
        if (!perTypeProperties.containsKey(object.getType())) {
            return new Variable(null);
        }

        return perTypeProperties.get(object.getType()).exposePropertyFor(this, object, property);
    }
}
