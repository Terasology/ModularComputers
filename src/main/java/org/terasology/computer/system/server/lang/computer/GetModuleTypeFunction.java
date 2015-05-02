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

        addExample("This example iterates over all module slots this computer has and prints out the type of the module " +
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
