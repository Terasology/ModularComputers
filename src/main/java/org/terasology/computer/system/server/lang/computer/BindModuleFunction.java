// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
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
