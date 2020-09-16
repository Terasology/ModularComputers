// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.modularcomputers.module.inventory;

import org.terasology.modularcomputers.FunctionParamValidationUtil;
import org.terasology.modularcomputers.context.ComputerCallback;
import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.ExecutionException;
import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.Variable;
import org.terasology.modularcomputers.system.server.lang.AbstractModuleMethodExecutable;

import java.util.Map;

public class InventorySlotCountMethod extends AbstractModuleMethodExecutable<Object> {

    private final String methodName;

    public InventorySlotCountMethod(String methodName) {
        super("Queries the specified inventory to check how many slots it has available.", "Number",
                "Number of slots in the inventory specified.");
        this.methodName = methodName;

        addParameter("inventoryBinding", "InventoryBinding", "Inventory it should query for number of slots.");

        addExample("This example creates output inventory binding to an inventory above it and prints out the slot " +
                        "count for it. Please make sure " +
                        "this computer has a module of Inventory Manipulator type in any of its slots.",
                "var invBind = computer.bindModuleOfType(\"" + InventoryModuleCommonSystem.COMPUTER_INVENTORY_MODULE_TYPE + "\");\n" +
                        "var topInv = invBind.getOutputInventoryBinding(\"up\");\n" +
                        "console.append(\"Inventory above has \" + invBind.getInventorySlotCount(topInv) + \" number " +
                        "of slots available for output.\");"
        );
    }

    @Override
    public int getCpuCycleDuration() {
        return 50;
    }

    @Override
    public Object onFunctionEnd(int line, ComputerCallback computer, Map<String, Variable> parameters,
                                Object onFunctionStartResult) throws ExecutionException {
        InventoryBinding.InventoryWithSlots inventory = FunctionParamValidationUtil.validateInventoryBinding(line,
                computer,
                parameters, "inventoryBinding", methodName, null);

        return inventory.slots.size();
    }
}
