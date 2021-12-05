// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.computer.module.inventory;

import com.gempukku.lang.ExecutionException;
import org.terasology.computer.context.ComputerCallback;
import org.terasology.engine.entitySystem.entity.EntityRef;

import java.util.List;

public interface InventoryBinding {
    InventoryWithSlots getInventoryEntity(int line, ComputerCallback computerCallback) throws ExecutionException;

    boolean isInput();

    class InventoryWithSlots {
        public final EntityRef inventory;
        public final List<Integer> slots;

        public InventoryWithSlots(EntityRef inventory, List<Integer> slots) {
            this.inventory = inventory;
            this.slots = slots;
        }
    }
}
