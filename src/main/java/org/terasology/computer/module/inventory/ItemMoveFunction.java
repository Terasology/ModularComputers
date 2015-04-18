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

import com.gempukku.lang.CustomObject;
import com.gempukku.lang.ExecutionException;
import com.gempukku.lang.Variable;
import org.terasology.computer.context.ComputerCallback;
import org.terasology.computer.system.server.lang.ModuleFunctionExecutable;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.logic.inventory.InventoryComponent;
import org.terasology.logic.inventory.InventoryManager;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ItemMoveFunction implements ModuleFunctionExecutable {
    private InventoryManager inventoryManager;

    public ItemMoveFunction(InventoryManager inventoryManager) {
        this.inventoryManager = inventoryManager;
    }

    @Override
    public int getDuration() {
        return 50;
    }

    @Override
    public int getMinimumExecutionTicks() {
        return 0;
    }

    @Override
    public String[] getParameterNames() {
        return new String[] {"inventoryBindingFrom", "inventoryBindingTo", "slot"};
    }

    @Override
    public Object executeFunction(int line, ComputerCallback computer, Map<String, Variable> parameters) throws ExecutionException {
        Variable inventoryBindingFrom = parameters.get("inventoryBindingFrom");
        if (inventoryBindingFrom.getType() != Variable.Type.CUSTOM_OBJECT
                || !((CustomObject) inventoryBindingFrom.getValue()).getType().equals("INVENTORY_BINDING"))
            throw new ExecutionException(line, "Invalid inventoryBindingFrom in itemMove()");

        Variable inventoryBindingTo = parameters.get("inventoryBindingTo");
        if (inventoryBindingTo.getType() != Variable.Type.CUSTOM_OBJECT
                || !((CustomObject) inventoryBindingTo.getValue()).getType().equals("INVENTORY_BINDING"))
            throw new ExecutionException(line, "Invalid inventoryBindingTo in itemMove()");

        Variable slot = parameters.get("slot");
        if (slot.getType() != Variable.Type.NUMBER)
            throw new ExecutionException(line, "Invalid slot in itemMove()");

        int slotNo = ((Number) slot.getValue()).intValue();

        InventoryBinding bindingFrom = (InventoryBinding) inventoryBindingFrom.getValue();
        EntityRef inventoryFromEntity = bindingFrom.getInventoryEntity(line, computer);

        InventoryBinding bindingTo = (InventoryBinding) inventoryBindingTo.getValue();
        EntityRef inventoryToEntity = bindingTo.getInventoryEntity(line, computer);

        InventoryComponent inventoryFrom = inventoryFromEntity.getComponent(InventoryComponent.class);
        int slotFromCount = inventoryFrom.itemSlots.size();

        if (slotNo<0 || slotFromCount<=slotNo)
            throw new ExecutionException(line, "Slot number out of range in itemMove()");

        InventoryComponent inventoryTo = inventoryToEntity.getComponent(InventoryComponent.class);
        int slotToCount = inventoryTo.itemSlots.size();

        int itemCountBefore = InventoryModuleUtils.getItemCount(inventoryFrom.itemSlots.get(slotNo));

        List<Integer> slots = new LinkedList<>();
        for (int i=0; i<slotToCount; i++) {
            slots.add(i);
        }

        inventoryManager.moveItemToSlots(computer.getComputerEntity(), inventoryFromEntity, slotNo, inventoryToEntity, slots);

        int itemCountAfter = InventoryModuleUtils.getItemCount(inventoryFrom.itemSlots.get(slotNo));

        return itemCountBefore-itemCountAfter;
    }
}
