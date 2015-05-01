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
import org.terasology.computer.system.server.lang.ModuleMethodExecutable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ModuleFunctionAdapter implements FunctionExecutable {
    private int _slotNo;
    private ModuleMethodExecutable _moduleFunction;

    public ModuleFunctionAdapter(int slotNo, ModuleMethodExecutable moduleFunction) {
        _slotNo = slotNo;
        _moduleFunction = moduleFunction;
    }

    @Override
    public CallContext getCallContext() {
        return new CallContext(null, false, false);
    }

    @Override
    public Collection<String> getParameterNames() {
        return _moduleFunction.getParameterNames();
    }

    @Override
    public Execution createExecution(final int line, ExecutionContext executionContext, CallContext callContext) {
        final SimpleParameterExecution execution = new SimpleParameterExecution(line);

        int minimumExecutionTime;
        try {
            minimumExecutionTime = _moduleFunction.getMinimumExecutionTime(line, getComputerCallback(executionContext), getVariableMap(executionContext));
        } catch (ExecutionException e) {
            minimumExecutionTime = 0;
        }
        return new DelayedExecution(_moduleFunction.getCpuCycleDuration(), minimumExecutionTime, execution) {
            @Override
            protected void onExecutionStart(ExecutionContext executionContext) throws ExecutionException {
                Object result = _moduleFunction.onFunctionStart(line, getComputerCallback(executionContext), getVariableMap(executionContext));
                execution.setParameter(result);
            }
        };
    }

    private Map<String, Variable> getVariableMap(ExecutionContext context) {
        final Collection<String> parameterNames = getParameterNames();
        Map<String, Variable> parameters = new HashMap<String, Variable>();
        final CallContext callContext = context.peekCallContext();
        for (String parameterName : parameterNames) {
            Variable variableValue;
            try {
                variableValue = callContext.getVariableValue(parameterName);
            } catch (ExecutionException exp) {
                variableValue = new Variable(null);
            }
            parameters.put(parameterName, variableValue);
        }
        return parameters;
    }

    private ComputerCallback getComputerCallback(ExecutionContext context) {
        final TerasologyComputerExecutionContext terasologyExecutionContext = (TerasologyComputerExecutionContext) context;
        return terasologyExecutionContext.getComputerCallback();
    }

    private class SimpleParameterExecution extends SimpleExecution {
        private int line;
        private Object parameter;

        private SimpleParameterExecution(int line) {
            this.line = line;
        }

        public void setParameter(Object parameter) {
            this.parameter = parameter;
        }

        @Override
        protected ExecutionProgress execute(ExecutionContext context, ExecutionCostConfiguration configuration) throws ExecutionException {
            ComputerCallback computerCallback = getComputerCallback(context);

            Map<String, Variable> parameters = getVariableMap(context);

            context.setReturnValue(new Variable(_moduleFunction.onFunctionEnd(line, computerCallback, parameters, parameter)));
            return new ExecutionProgress(configuration.getSetReturnValue());
        }

    }
}
