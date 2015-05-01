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
import com.gempukku.lang.Execution;
import com.gempukku.lang.ExecutionContext;
import com.gempukku.lang.ExecutionCostConfiguration;
import com.gempukku.lang.ExecutionException;
import com.gempukku.lang.ExecutionProgress;
import com.gempukku.lang.FunctionExecutable;
import com.gempukku.lang.execution.SimpleExecution;
import org.terasology.computer.context.ComputerCallback;
import org.terasology.computer.context.TerasologyComputerExecutionContext;
import org.terasology.computer.system.server.lang.ComputerModule;

public class BindingFunctionWrapper implements FunctionExecutable {
    private int slotNo;
    private ComputerModule module;
    private FunctionExecutable function;

    public BindingFunctionWrapper(ComputerModule module, int slotNo, FunctionExecutable function) {
        this.module = module;
        this.slotNo = slotNo;
        this.function = function;
    }

    @Override
    public CallContext getCallContext() {
        return new CallContext(null, false, false);
    }

    @Override
    public java.util.Collection<String> getParameterNames() {
        return function.getParameterNames();
    }

    @Override
    public Execution createExecution(int line, ExecutionContext executionContext, final CallContext callContext) {
        final TerasologyComputerExecutionContext terasologyExecutionContext = (TerasologyComputerExecutionContext) executionContext;
        final ComputerCallback computerCallback = terasologyExecutionContext.getComputerCallback();

        final ComputerModule moduleAtSlot = computerCallback.getModule(slotNo);
        if (moduleAtSlot == module) {
            return function.createExecution(line, executionContext, callContext);
        } else {
            return getThrowingExceptionExecution(line);
        }
    }

    private Execution getThrowingExceptionExecution(final int line) {
        return new SimpleExecution() {
            @Override
            protected ExecutionProgress execute(ExecutionContext context, ExecutionCostConfiguration configuration) throws ExecutionException {
                throw new ExecutionException(line, "Bound module has been removed or replaced");
            }
        };
    }
}
