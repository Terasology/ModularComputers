// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
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
    private final int _slotNo;
    private final ModuleMethodExecutable _moduleFunction;

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
            minimumExecutionTime = _moduleFunction.getMinimumExecutionTime(line,
                    getComputerCallback(executionContext), getVariableMap(executionContext));
        } catch (ExecutionException e) {
            minimumExecutionTime = 0;
        }
        return new DelayedExecution(_moduleFunction.getCpuCycleDuration(), minimumExecutionTime, execution) {
            @Override
            protected void onExecutionStart(ExecutionContext executionContext) throws ExecutionException {
                Object result = _moduleFunction.onFunctionStart(line, getComputerCallback(executionContext),
                        getVariableMap(executionContext));
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
        final TerasologyComputerExecutionContext terasologyExecutionContext =
                (TerasologyComputerExecutionContext) context;
        return terasologyExecutionContext.getComputerCallback();
    }

    private class SimpleParameterExecution extends SimpleExecution {
        private final int line;
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

            context.setReturnValue(new Variable(_moduleFunction.onFunctionEnd(line, computerCallback, parameters,
                    parameter)));
            return new ExecutionProgress(configuration.getSetReturnValue());
        }

    }
}
