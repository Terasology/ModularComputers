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
package org.terasology.computer.module.inventory;

import com.gempukku.lang.ExecutionException;
import com.gempukku.lang.Variable;
import org.terasology.computer.FunctionParamValidationUtil;
import org.terasology.computer.context.ComputerCallback;
import org.terasology.computer.system.server.lang.AbstractModuleMethodExecutable;
import org.terasology.module.inventory.systems.InventoryManager;
import org.terasology.module.inventory.systems.InventoryUtils;

import java.util.Map;

public class ItemMoveMethod extends AbstractModuleMethodExecutable<Object> {
    private final String methodName;
    private InventoryManager inventoryManager;

    public ItemMoveMethod(String methodName, InventoryManager inventoryManager) {
        super("Moves item(s) at the specified slot in the \"from\" inventory to the \"to\" inventory.", "Number",
                "Number of items that was successfully moved.");
        this.inventoryManager = inventoryManager;
        this.methodName = methodName;

        addParameter("inventoryBindingFrom", "InventoryBinding", "Inventory it should extract the item from.");
        addParameter("inventoryBindingTo", "InventoryBinding", "Inventory it should insert the item to.");
        addParameter("slot", "Number", "Slot number of the \"from\" inventory it should extract item from.");

        addExample(
                "This example moves item from the first slot of an inventory above the computer to inventory to the east of the computer. Please make sure " +
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
    public Object onFunctionEnd(int line, ComputerCallback computer, Map<String, Variable> parameters, Object onFunctionStartResult) throws ExecutionException {
        InventoryBinding.InventoryWithSlots inventoryFrom = FunctionParamValidationUtil.validateInventoryBinding(line, computer,
                parameters, "inventoryBindingFrom", methodName, false);
        InventoryBinding.InventoryWithSlots inventoryTo = FunctionParamValidationUtil.validateInventoryBinding(line, computer,
                parameters, "inventoryBindingTo", methodName, true);

        int slotNo = FunctionParamValidationUtil.validateSlotNo(line, parameters, inventoryFrom, "slot", methodName);

        int itemCountBefore = InventoryModuleUtils.getItemCount(InventoryUtils.getItemAt(inventoryFrom.inventory, inventoryFrom.slots.get(slotNo)));

        inventoryManager.moveItemToSlots(computer.getComputerEntity(), inventoryFrom.inventory, slotNo, inventoryTo.inventory, inventoryTo.slots);

        int itemCountAfter = InventoryModuleUtils.getItemCount(InventoryUtils.getItemAt(inventoryFrom.inventory, inventoryFrom.slots.get(slotNo)));

        return itemCountBefore - itemCountAfter;
    }
}
