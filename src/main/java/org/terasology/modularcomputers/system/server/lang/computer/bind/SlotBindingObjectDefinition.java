// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.modularcomputers.system.server.lang.computer.bind;


import org.terasology.modularcomputers.context.ComputerCallback;
import org.terasology.modularcomputers.context.TerasologyComputerExecutionContext;
import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.ExecutionContext;
import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.ObjectDefinition;
import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.Variable;
import org.terasology.modularcomputers.system.server.lang.ComputerModule;
import org.terasology.modularcomputers.system.server.lang.ModuleMethodExecutable;

public class SlotBindingObjectDefinition implements ObjectDefinition {
    private final int _slotNo;

    public SlotBindingObjectDefinition(int slotNo) {
        _slotNo = slotNo;
    }

    @Override
    public Variable getMember(ExecutionContext context, String name) {
        final TerasologyComputerExecutionContext terasologyExecutionContext =
                (TerasologyComputerExecutionContext) context;
        final ComputerCallback computerCallback = terasologyExecutionContext.getComputerCallback();

        final ComputerModule module = computerCallback.getModule(_slotNo);
        if (module == null)
            return new Variable(null);

        final ModuleMethodExecutable moduleFunction = module.getMethodByName(name);

        if (moduleFunction != null) {
            return new Variable(new BindingFunctionWrapper(module, _slotNo, new ModuleFunctionAdapter(_slotNo,
                    moduleFunction)));
        } else {
            return new Variable(null);
        }
    }
}
