// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.computer.system.server.lang.computer.bind;


import com.gempukku.lang.ExecutionContext;
import com.gempukku.lang.ObjectDefinition;
import com.gempukku.lang.Variable;
import org.terasology.computer.context.ComputerCallback;
import org.terasology.computer.context.TerasologyComputerExecutionContext;
import org.terasology.computer.system.server.lang.ComputerModule;
import org.terasology.computer.system.server.lang.ModuleMethodExecutable;

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
