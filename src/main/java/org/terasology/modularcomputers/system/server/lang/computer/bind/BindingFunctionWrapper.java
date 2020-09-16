// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.modularcomputers.system.server.lang.computer.bind;

import org.terasology.modularcomputers.context.ComputerCallback;
import org.terasology.modularcomputers.context.TerasologyComputerExecutionContext;
import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.CallContext;
import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.Execution;
import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.ExecutionContext;
import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.ExecutionCostConfiguration;
import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.ExecutionException;
import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.ExecutionProgress;
import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.FunctionExecutable;
import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.execution.SimpleExecution;
import org.terasology.modularcomputers.system.server.lang.ComputerModule;

public class BindingFunctionWrapper implements FunctionExecutable {
    private final int slotNo;
    private final ComputerModule module;
    private final FunctionExecutable function;

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
        final TerasologyComputerExecutionContext terasologyExecutionContext =
                (TerasologyComputerExecutionContext) executionContext;
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
