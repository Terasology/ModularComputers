/*
 * Copyright 2015 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.terasology.computer.system.server.lang;

import com.gempukku.lang.CallContext;
import com.gempukku.lang.DelayedExecution;
import com.gempukku.lang.Execution;
import com.gempukku.lang.ExecutionContext;
import com.gempukku.lang.ExecutionCostConfiguration;
import com.gempukku.lang.ExecutionException;
import com.gempukku.lang.ExecutionProgress;
import com.gempukku.lang.FunctionExecutable;
import com.gempukku.lang.Variable;
import com.gempukku.lang.execution.SimpleExecution;
import org.terasology.computer.context.ComputerCallback;
import org.terasology.computer.context.TerasologyComputerExecutionContext;

import java.util.HashMap;
import java.util.Map;

public abstract class TerasologyFunctionExecutable implements FunctionExecutable {
    @Override
    public final Execution createExecution(final int line, ExecutionContext executionContext, CallContext callContext) {
        return new DelayedExecution(getDuration(), 0,
                new SimpleExecution() {
                    @Override
                    protected ExecutionProgress execute(ExecutionContext context, ExecutionCostConfiguration configuration) throws ExecutionException {
                        final TerasologyComputerExecutionContext terasologyExecutionContext = (TerasologyComputerExecutionContext) context;
                        ComputerCallback computer = terasologyExecutionContext.getComputerCallback();

                        final String[] parameterNames = getParameterNames();
                        Map<String, Variable> parameters = new HashMap<String, Variable>();
                        final CallContext callContext = context.peekCallContext();
                        for (String parameterName : parameterNames)
                            parameters.put(parameterName, callContext.getVariableValue(parameterName));

                        context.setReturnValue(new Variable(executeFunction(line, computer, parameters)));
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
     * Executes this function, gets passed an instance of the computer this function is executed on, as well as
     * parameters passed to the function, as defined by getParameterNames method in this class. The returned object
     * will be placed into context that called this function. It is advisable to use only basic objects as return values.
     * Numbers (int, float), booleans, Strings and null.
     * If an execution of the program should be stopped due to a fatal exception, ExecutionException should be thrown by
     * the method.
     *
     * @param line       Line where the call to the function was made.
     * @param computer   Computer this function is executed on.
     * @param parameters Parameters that were sent to this function.
     * @return Object that has to be set in context of the caller (return value).
     * @throws ExecutionException Fatal exception that will be communicated to the computer console. When thrown,
     *                            the execution of the program will stop.
     */
    protected abstract Object executeFunction(int line, ComputerCallback computer, Map<String, Variable> parameters) throws ExecutionException;
}