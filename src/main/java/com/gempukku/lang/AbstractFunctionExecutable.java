// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package com.gempukku.lang;


import com.gempukku.lang.execution.SimpleExecution;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractFunctionExecutable implements FunctionExecutable {
    @Override
    public final Execution createExecution(final int line, ExecutionContext executionContext, CallContext callContext) {
        return new DelayedExecution(getDuration(), 0,
                new SimpleExecution() {
                    @Override
                    protected ExecutionProgress execute(ExecutionContext context,
                                                        ExecutionCostConfiguration configuration) throws ExecutionException {
                        final Iterable<String> parameterNames = getParameterNames();
                        Map<String, Variable> parameters = new HashMap<>();
                        final CallContext callContext = context.peekCallContext();
                        for (String parameterName : parameterNames) {
                            parameters.put(parameterName, callContext.getVariableValue(parameterName));
                        }

                        context.setReturnValue(new Variable(executeFunction(line, parameters)));
                        return new ExecutionProgress(configuration.getSetReturnValue());
                    }
                });
    }

    @Override
    public final CallContext getCallContext() {
        return new CallContext(null, false, false);
    }

    /**
     * Returns duration of the operation in computer cycles. Used to effectively throttle computer programs.
     *
     * @return Duration in computer cycles.
     */
    protected abstract int getDuration();

    /**
     * Executes this function, gets passed parameters passed to the function, as defined by getParameterNames method in
     * this class. The returned object will be placed into context that called this function. It is advisable to use
     * only basic objects as return values. Numbers (int, float), booleans, Strings and null. If an execution of the
     * program should be stopped due to a fatal exception, ExecutionException should be thrown by the method.
     *
     * @param line Line where the call to the function was made.
     * @param parameters Parameters that were sent to this function.
     * @return Object that has to be set in context of the caller (return value).
     * @throws ExecutionException Fatal exception that will be communicated to the computer console. When
     *         thrown, the execution of the program will stop.
     */
    protected abstract Object executeFunction(int line, Map<String, Variable> parameters) throws ExecutionException;
}
