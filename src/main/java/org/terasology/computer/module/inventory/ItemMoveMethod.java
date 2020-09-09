// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.computer.module.inventory;

import com.gempukku.lang.ExecutionException;
import com.gempukku.lang.Variable;
import org.terasology.computer.FunctionParamValidationUtil;
import org.terasology.computer.context.ComputerCallback;
import org.terasology.computer.system.server.lang.AbstractModuleMethodExecutable;
import org.terasology.inventory.logic.InventoryManager;
import org.terasology.inventory.logic.InventoryUtils;

import java.util.Map;

public class ItemMoveMethod extends AbstractModuleMethodExecutable<Object> {
    private final String methodName;
    private final InventoryManager inventoryManager;

    public ItemMoveMethod(String methodName, InventoryManager inventoryManager) {
        super("Moves item(s) at the specified slot in the \"from\" inventory to the \"to\" inventory.", "Number",
                "Number of items that was successfully moved.");
        this.inventoryManager = inventoryManager;
        this.methodName = methodName;

        addParameter("inventoryBindingFrom", "InventoryBinding", "Inventory it should extract the item from.");
        addParameter("inventoryBindingTo", "InventoryBinding", "Inventory it should insert the item to.");
        addParameter("slot", "Number", "Slot number of the \"from\" inventory it should extract item from.");

        addExample(
                "This example moves item from the first slot of an inventory above the computer to inventory to the " +
                        "east of the computer. Please make sure " +
                        "this computer has a module of Inventory Manipulator type in any of its slots.",
                "var invBind = computer.bindModuleOfType(\"" + InventoryModuleCommonSystem.COMPUTER_INVENTORY_MODULE_TYPE + "\");\n" +
                        "var topInv = invBind.getOutputInventoryBinding(\"up\");\n" +
                        "var eastInv = invBind.getInputInventoryBinding(\"east\");\n" +
                        "invBind.itemMove(topInv, eastInv, 0);"
        );
    }

    @Override
    public int getCpuCycleDuration() {
        return 50;
    }

    @Override
    public Object onFunctionEnd(int line, ComputerCallback computer, Map<String, Variable> parameters,
                                Object onFunctionStartResult) throws ExecutionException {
        InventoryBinding.InventoryWithSlots inventoryFrom = FunctionParamValidationUtil.validateInventoryBinding(line
                , computer,
                parameters, "inventoryBindingFrom", methodName, false);
        InventoryBinding.InventoryWithSlots inventoryTo = FunctionParamValidationUtil.validateInventoryBinding(line,
                computer,
                parameters, "inventoryBindingTo", methodName, true);

        int slotNo = FunctionParamValidationUtil.validateSlotNo(line, parameters, inventoryFrom, "slot", methodName);

        int itemCountBefore = InventoryModuleUtils.getItemCount(InventoryUtils.getItemAt(inventoryFrom.inventory,
                inventoryFrom.slots.get(slotNo)));

        inventoryManager.moveItemToSlots(computer.getComputerEntity(), inventoryFrom.inventory, slotNo,
                inventoryTo.inventory, inventoryTo.slots);

        int itemCountAfter = InventoryModuleUtils.getItemCount(InventoryUtils.getItemAt(inventoryFrom.inventory,
                inventoryFrom.slots.get(slotNo)));

        return itemCountBefore - itemCountAfter;
    }
}
