// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.computer.system.server.lang.computer;


import com.gempukku.lang.ExecutionException;
import com.gempukku.lang.Variable;
import org.terasology.computer.FunctionParamValidationUtil;
import org.terasology.computer.context.ComputerCallback;
import org.terasology.computer.system.server.lang.ComputerModule;
import org.terasology.computer.system.server.lang.TerasologyFunctionExecutable;

import java.util.Map;

public class GetModuleTypeFunction extends TerasologyFunctionExecutable {
    public GetModuleTypeFunction() {
        super("Returns the module type at the specified slot.", "String",
                "Module type at the slot specified, or null if no module at that slot is present.");

        addParameter("slot", "Number", "Slot to check for module type.");

        addExample("This example iterates over all module slots this computer has and prints out the type of the " +
                        "module " +
                        "in that slot.",
                "var moduleSlotCount = computer.getModuleSlotCount();\n" +
                        "for (var i=0; i < moduleSlotCount; i++) {\n" +
                        "  var moduleType = computer.getModuleType(i);\n" +
                        "  if (moduleType == null)\n" +
                        "    console.append(\"Slot \"+(i+1)+\" has no module.\");\n" +
                        "  else\n" +
                        "    console.append(\"Slot \"+(i+1)+\" has module of type - \" + moduleType);\n" +
                        "}"
        );
    }

    @Override
    protected int getDuration() {
        return 100;
    }

    @Override
    protected Object executeFunction(int line, ComputerCallback computer, Map<String, Variable> parameters) throws ExecutionException {
        int slotNo = FunctionParamValidationUtil.validateIntParameter(line, parameters, "slot", "getModuleType");

        final ComputerModule module = computer.getModule(slotNo);
        String moduleType = null;
        if (module != null) {
            moduleType = module.getModuleType();
        }
        return moduleType;
    }
}
