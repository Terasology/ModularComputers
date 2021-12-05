// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.computer.module.storage;

import com.gempukku.lang.CustomObject;
import com.gempukku.lang.ExecutionException;
import org.terasology.computer.context.ComputerCallback;
import org.terasology.computer.module.inventory.InventoryBinding;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.module.inventory.components.InventoryComponent;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class InternalInventoryBindingCustomObject implements CustomObject, InventoryBinding {
    private boolean input;

    public InternalInventoryBindingCustomObject(boolean input) {
        this.input = input;
    }

    @Override
    public Collection<String> getType() {
        return Collections.singleton("INVENTORY_BINDING");
    }

    @Override
    public InventoryWithSlots getInventoryEntity(int line, ComputerCallback computerCallback) throws ExecutionException {
        EntityRef inventoryEntity = computerCallback.getComputerEntity().getComponent(InternalStorageComponent.class).inventoryEntity;
        int slotCount = inventoryEntity.getComponent(InventoryComponent.class).itemSlots.size();
        List<Integer> slots = getSlotList(slotCount);
        return new InventoryWithSlots(inventoryEntity, Collections.unmodifiableList(slots));
    }

    @Override
    public int sizeOf() {
        return 4;
    }

    @Override
    public boolean isInput() {
        return input;
    }

    private List<Integer> getSlotList(int slotCount) {
        List<Integer> slots = new LinkedList<>();
        for (int i = 0; i < slotCount; i++) {
            slots.add(i);
        }
        return slots;
    }
}
