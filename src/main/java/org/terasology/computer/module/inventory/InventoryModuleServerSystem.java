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

import org.terasology.computer.system.server.ComputerModuleRegistry;
import org.terasology.computer.system.server.lang.os.condition.AbstractConditionCustomObject;
import org.terasology.computer.system.server.lang.os.condition.LatchCondition;
import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.entity.lifecycleEvents.BeforeDeactivateComponent;
import org.terasology.entitySystem.entity.lifecycleEvents.OnActivatedComponent;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.logic.common.ActivateEvent;
import org.terasology.logic.inventory.InventoryComponent;
import org.terasology.logic.inventory.InventoryManager;
import org.terasology.logic.inventory.events.InventorySlotChangedEvent;
import org.terasology.logic.inventory.events.InventorySlotStackSizeChangedEvent;
import org.terasology.registry.In;
import org.terasology.world.BlockEntityRegistry;
import org.terasology.world.WorldProvider;

import java.util.HashMap;
import java.util.Map;

@RegisterSystem(RegisterMode.AUTHORITY)
public class InventoryModuleServerSystem extends BaseComponentSystem implements InventoryModuleConditionsRegister {
    public static final String COMPUTER_INVENTORY_MODULE_TYPE = "Inventory";

    @In
    private ComputerModuleRegistry computerModuleRegistry;
    @In
    private BlockEntityRegistry blockEntityRegistry;
    @In
    private InventoryManager inventoryManager;

    private Map<EntityRef, LatchCondition> inventoryChangeLatchConditions = new HashMap<>();

    @Override
    public void preBegin() {
        computerModuleRegistry.registerComputerModule(
                COMPUTER_INVENTORY_MODULE_TYPE,
                new InventoryComputerModule(this, inventoryManager,
                        blockEntityRegistry, COMPUTER_INVENTORY_MODULE_TYPE, "Inventory manipulator"));
    }

    @Override
    public AbstractConditionCustomObject registerInventoryChangeListener(EntityRef entity) {
        LatchCondition latchCondition = inventoryChangeLatchConditions.get(entity);
        if (latchCondition != null)
            return latchCondition;

        latchCondition = new LatchCondition();
        inventoryChangeLatchConditions.put(entity, latchCondition);

        return latchCondition;
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
        processChangedInventory(inventory);
    }

    @ReceiveEvent
    public void inventoryAdded(OnActivatedComponent event, EntityRef inventory, InventoryComponent inventoryComponent) {
        processChangedInventory(inventory);
    }

    private void processChangedInventory(EntityRef inventory) {
        LatchCondition latchCondition = inventoryChangeLatchConditions.remove(inventory);
        if (latchCondition != null) {
            latchCondition.release(null);
        }
    }
}
