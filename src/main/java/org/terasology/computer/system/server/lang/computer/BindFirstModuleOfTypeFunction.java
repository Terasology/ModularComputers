// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.computer.system.server.lang.computer;

import com.gempukku.lang.ExecutionException;
import com.gempukku.lang.Variable;
import org.terasology.computer.FunctionParamValidationUtil;
import org.terasology.computer.context.ComputerCallback;
import org.terasology.computer.system.server.lang.ComputerModule;
import org.terasology.computer.system.server.lang.TerasologyFunctionExecutable;
import org.terasology.computer.system.server.lang.computer.bind.SlotBindingObjectDefinition;

import java.util.Map;

public class BindFirstModuleOfTypeFunction extends TerasologyFunctionExecutable {
    public BindFirstModuleOfTypeFunction() {
        super("Binds first module of the specified type in any of the slots.", "Object",
                "Binding to the module. " +
                        "This object exposes all the methods, as described in documentation for the module. " +
                "If the module is not found in this computer, this function returns null.");

        addParameter("type", "String", "Type of the module to bind.");

        addExample("This example binds first module of the specified type that it finds in computer's module slots to the variable " +
                        "and then executes \"move\" method on it with \"up\" parameter. " +
                        "In order for successful execution - please place \"Mobility\" module in any slot of the computer.",
                "var moduleBinding = computer.bindModuleOfType(\"Mobility\");\n" +
                        "moduleBinding.move(\"up\");"
        );
    }

    @Override
    protected int getDuration() {
        return 100;
    }

    @Override
    protected Object executeFunction(int line, ComputerCallback computer, Map<String, Variable> parameters) throws ExecutionException {
        String moduleType = FunctionParamValidationUtil.validateStringParameter(line, parameters, "type", "bindModuleOfType");

        final int moduleSlotsCount = computer.getModuleSlotsCount();
        for (int i = 0; i < moduleSlotsCount; i++) {
            final ComputerModule module = computer.getModule(i);
            if (module != null && module.getModuleType().equals(moduleType)) {
                return new SlotBindingObjectDefinition(i);
            }
        }
        throw new ExecutionException(line, "Couldn't find module of the specified type.");
    }
}
