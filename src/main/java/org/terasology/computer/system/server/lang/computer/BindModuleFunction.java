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
import org.terasology.computer.system.server.lang.TerasologyFunctionExecutable;
import org.terasology.computer.system.server.lang.computer.bind.SlotBindingObjectDefinition;

import java.util.Map;

public class BindModuleFunction extends TerasologyFunctionExecutable {
    public BindModuleFunction() {
        super("Binds the module specified in the slot number.", "Object",
                "Binding to the module. This object exposes all the methods, as described in documentation for the module.");

        addParameter("slot", "Number", "Slot number of a module to bind.");

        addExample("This example binds module in the first slot and executes \"move\" method on it with \"up\" parameter. " +
                        "In order for successful execution - please place \"Mobility\" module in the first slot of the computer.",
                "var moduleBinding = computer.bindModule(0);\n" +
                        "moduleBinding.move(\"up\");"
        );
    }

    @Override
    protected int getDuration() {
        return 100;
    }

    @Override
    protected Object executeFunction(int line, ComputerCallback computer, Map<String, Variable> parameters) throws ExecutionException {
        int slotNo = FunctionParamValidationUtil.validateIntParameter(line, parameters, "slot", "bindModule");

        if (slotNo < 0 || slotNo >= computer.getModuleSlotsCount()) {
            throw new ExecutionException(line, "Slot number outside of permitted range in bindModule()");
        }

        return new SlotBindingObjectDefinition(slotNo);
    }
}
