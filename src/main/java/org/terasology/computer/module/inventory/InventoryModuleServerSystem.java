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

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import org.terasology.computer.system.server.lang.os.condition.InventoryCondition;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.entity.lifecycleEvents.BeforeDeactivateComponent;
import org.terasology.engine.entitySystem.event.ReceiveEvent;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterMode;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.module.inventory.components.InventoryComponent;
import org.terasology.module.inventory.events.InventorySlotChangedEvent;
import org.terasology.module.inventory.events.InventorySlotStackSizeChangedEvent;
import org.terasology.engine.registry.Share;

import java.util.Iterator;

@RegisterSystem(RegisterMode.AUTHORITY)
@Share(InventoryModuleConditionsRegister.class)
public class InventoryModuleServerSystem extends BaseComponentSystem implements InventoryModuleConditionsRegister {
    private Multimap<EntityRef, InventoryCondition> inventoryChangeLatchConditions = HashMultimap.create();

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
    public void inventoryRemoved(BeforeDeactivateComponent event, EntityRef inventory, InventoryComponent inventoryComponent) {
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
