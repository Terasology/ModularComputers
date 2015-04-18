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
package org.terasology.computer.system.server.lang.computer.bind;

import com.gempukku.lang.*;
import com.gempukku.lang.execution.SimpleExecution;
import org.terasology.computer.context.ComputerCallback;
import org.terasology.computer.context.TerasologyComputerExecutionContext;
import org.terasology.computer.system.server.lang.ModuleFunctionExecutable;

import java.util.HashMap;
import java.util.Map;

public class ModuleFunctionAdapter implements FunctionExecutable {
    private int _slotNo;
    private ModuleFunctionExecutable _moduleFunction;

    public ModuleFunctionAdapter(int slotNo, ModuleFunctionExecutable moduleFunction) {
        _slotNo = slotNo;
        _moduleFunction = moduleFunction;
    }

    @Override
    public CallContext getCallContext() {
        return new CallContext(null, false, false);
    }

    @Override
    public String[] getParameterNames() {
        return _moduleFunction.getParameterNames();
    }

    @Override
    public Execution createExecution(final int line, ExecutionContext executionContext, CallContext callContext) {
        return new DelayedExecution(_moduleFunction.getDuration(), _moduleFunction.getMinimumExecutionTicks(),
                new SimpleExecution() {
                    @Override
                    protected ExecutionProgress execute(ExecutionContext context, ExecutionCostConfiguration configuration) throws ExecutionException {
                        final TerasologyComputerExecutionContext minecraftExecutionContext = (TerasologyComputerExecutionContext) context;
                        ComputerCallback computerCallback = minecraftExecutionContext.getComputerCallback();

                        final String[] parameterNames = getParameterNames();
                        Map<String, Variable> parameters = new HashMap<String, Variable>();
                        final CallContext callContext = context.peekCallContext();
                        for (String parameterName : parameterNames)
                            parameters.put(parameterName, callContext.getVariableValue(parameterName));

                        context.setReturnValue(new Variable(_moduleFunction.executeFunction(line, computerCallback, minecraftExecutionContext.getModuleComputerCallback(_slotNo), parameters)));
                        return new ExecutionProgress(configuration.getSetReturnValue());
                    }
                });
    }
}
