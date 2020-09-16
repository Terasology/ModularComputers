// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.modularcomputers.module.inventory;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.entity.lifecycleEvents.BeforeDeactivateComponent;
import org.terasology.engine.entitySystem.event.ReceiveEvent;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterMode;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.registry.Share;
import org.terasology.inventory.logic.InventoryComponent;
import org.terasology.inventory.logic.events.InventorySlotChangedEvent;
import org.terasology.inventory.logic.events.InventorySlotStackSizeChangedEvent;
import org.terasology.modularcomputers.system.server.lang.os.condition.InventoryCondition;

import java.util.Iterator;

@RegisterSystem(RegisterMode.AUTHORITY)
@Share(InventoryModuleConditionsRegister.class)
public class InventoryModuleServerSystem extends BaseComponentSystem implements InventoryModuleConditionsRegister {
    private final Multimap<EntityRef, InventoryCondition> inventoryChangeLatchConditions = HashMultimap.create();

    @Override
    public void addInventoryChangeListener(EntityRef entity, InventoryCondition latchCondition) {
        inventoryChangeLatchConditions.put(entity, latchCondition);
    }

    @Override
    public void removeInventoryChangeListener(EntityRef entity, InventoryCondition latchCondition) {
        inventoryChangeLatchConditions.remove(entity, latchCondition);
    }

    @ReceiveEvent
    public void inventoryChange(InventorySlotChangedEvent event, EntityRef inventory) {
        processChangedInventory(inventory);
    }

    @ReceiveEvent
    public void inventoryChange(InventorySlotStackSizeChangedEvent event, EntityRef inventory) {
        processChangedInventory(inventory);
    }

    @ReceiveEvent
    public void inventoryRemoved(BeforeDeactivateComponent event, EntityRef inventory,
                                 InventoryComponent inventoryComponent) {
        Iterator<InventoryCondition> latchIterator = inventoryChangeLatchConditions.get(inventory).iterator();
        while (latchIterator.hasNext()) {
            InventoryCondition latchCondition = latchIterator.next();
            latchIterator.remove();
            latchCondition.cancelCondition();
        }
    }

    private void processChangedInventory(EntityRef inventory) {
        Iterator<InventoryCondition> latchIterator = inventoryChangeLatchConditions.get(inventory).iterator();
        while (latchIterator.hasNext()) {
            InventoryCondition latchCondition = latchIterator.next();
            if (latchCondition.checkRelease()) {
                latchIterator.remove();
            }
        }
    }
}
