// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.modularcomputers.module.inventory;

import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.modularcomputers.context.ComputerCallback;
import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.ExecutionException;

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
