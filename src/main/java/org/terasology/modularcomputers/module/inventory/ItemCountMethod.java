// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.modularcomputers.module.inventory;

import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.inventory.logic.InventoryUtils;
import org.terasology.modularcomputers.FunctionParamValidationUtil;
import org.terasology.modularcomputers.context.ComputerCallback;
import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.ExecutionException;
import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.Variable;
import org.terasology.modularcomputers.system.server.lang.AbstractModuleMethodExecutable;

import java.util.Map;

public class ItemCountMethod extends AbstractModuleMethodExecutable<Object> {

    private final String methodName;

    public ItemCountMethod(String methodName) {
        super("Checks how many items are in the specified inventory's slot.", "Number", "Number of items in the " +
                "specified slot in the inventory.");
        this.methodName = methodName;

        addParameter("inventoryBinding", "InventoryBinding", "Inventory it should check for the amount of items.");
        addParameter("slot", "Number", "Slot it should check for number of items.");

        addExample("This example creates output inventory binding to an inventory above it and prints out number of " +
                        "items in its first slot. Please make sure " +
                        "this computer has a module of Inventory Manipulator type in any of its slots.",
                "var invBind = computer.bindModuleOfType(\"" + InventoryModuleCommonSystem.COMPUTER_INVENTORY_MODULE_TYPE + "\");\n" +
                        "var topInv = invBind.getOutputInventoryBinding(\"up\");\n" +
                        "console.append(\"Inventory above has \" + invBind.getItemCount(topInv, 0) + \" number of " +
                        "items in its first output slot.\");");
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

        int slotNo = FunctionParamValidationUtil.validateSlotNo(line, parameters, inventory, "slot", methodName);

        EntityRef itemEntity = InventoryUtils.getItemAt(inventory.inventory, inventory.slots.get(slotNo));
        return InventoryModuleUtils.getItemCount(itemEntity);
    }
}
