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
import org.terasology.computer.system.server.lang.ModuleMethodExecutable;
import org.terasology.logic.inventory.InventoryManager;
import org.terasology.logic.inventory.InventoryUtils;

import java.util.Map;

public class ItemMoveMethod implements ModuleMethodExecutable<Object> {
    private final String methodName;
    private InventoryManager inventoryManager;

    public ItemMoveMethod(String methodName, InventoryManager inventoryManager) {
        this.inventoryManager = inventoryManager;
        this.methodName = methodName;
    }

    @Override
    public int getCpuCycleDuration() {
        return 50;
    }

    @Override
    public String[] getParameterNames() {
        return new String[]{"inventoryBindingFrom", "inventoryBindingTo", "slot"};
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
