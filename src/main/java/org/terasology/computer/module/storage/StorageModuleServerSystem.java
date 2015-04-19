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
package org.terasology.computer.module.storage;

import org.terasology.computer.component.ComputerComponent;
import org.terasology.computer.component.ComputerModuleComponent;
import org.terasology.computer.system.server.ComputerModuleRegistry;
import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.EventPriority;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.logic.inventory.InventoryComponent;
import org.terasology.logic.inventory.PickupBuilder;
import org.terasology.logic.inventory.events.InventorySlotChangedEvent;
import org.terasology.math.geom.Vector3i;
import org.terasology.physics.events.ImpulseEvent;
import org.terasology.registry.In;
import org.terasology.utilities.random.FastRandom;
import org.terasology.world.block.BlockComponent;
import org.terasology.world.block.items.OnBlockToItem;

@RegisterSystem(RegisterMode.AUTHORITY)
public class StorageModuleServerSystem extends BaseComponentSystem {
    public static final String COMPUTER_STORAGE_MODULE_TYPE = "Storage";

    @In
    private ComputerModuleRegistry computerModuleRegistry;
    @In
    private EntityManager entityManager;

    @Override
    public void preBegin() {
        computerModuleRegistry.registerComputerModule(
                COMPUTER_STORAGE_MODULE_TYPE,
                new StorageComputerModule(COMPUTER_STORAGE_MODULE_TYPE, "Internal storage", 9));
    }

    @ReceiveEvent
    public void computerModuleSlotChanged(InventorySlotChangedEvent event, EntityRef computerEntity, ComputerComponent computer, BlockComponent block) {
        ComputerModuleComponent oldModule = event.getOldItem().getComponent(ComputerModuleComponent.class);
        if (oldModule != null && oldModule.moduleType.equals(COMPUTER_STORAGE_MODULE_TYPE)) {
            dropItemsFromComputerInternalStorage(computerEntity);
        }

        ComputerModuleComponent newModule = event.getNewItem().getComponent(ComputerModuleComponent.class);
        if (newModule != null && newModule.moduleType.equals(COMPUTER_STORAGE_MODULE_TYPE)) {
            EntityRef storageEntity = entityManager.create();

            InternalStorageComponent internalStorage = new InternalStorageComponent();
            internalStorage.inventoryEntity = storageEntity;

            InventoryComponent inventoryComponent = new InventoryComponent(9);
            storageEntity.addComponent(inventoryComponent);

            computerEntity.addComponent(internalStorage);
        }
    }

    private void dropItemsFromComputerInternalStorage(EntityRef computerEntity) {
        InternalStorageComponent internalStorage = computerEntity.getComponent(InternalStorageComponent.class);
        Vector3i blockLocation = computerEntity.getComponent(BlockComponent.class).getPosition();

        EntityRef inventoryEntity = internalStorage.inventoryEntity;
        InventoryComponent inventoryComponent = inventoryEntity.getComponent(InventoryComponent.class);

        FastRandom random = new FastRandom();
        PickupBuilder pickupBuilder = new PickupBuilder(entityManager);
        for (EntityRef itemSlot : inventoryComponent.itemSlots) {
            if (itemSlot.exists()) {
                EntityRef pickup = pickupBuilder.createPickupFor(itemSlot, blockLocation.toVector3f(), 60, true);
                pickup.send(new ImpulseEvent(random.nextVector3f(30.0f)));
            }
        }

        inventoryEntity.destroy();
    }

    @ReceiveEvent(priority = EventPriority.PRIORITY_TRIVIAL)
    public void computerDestroyed(OnBlockToItem event, EntityRef computerEntity, ComputerComponent computer) {
        InventoryComponent component = computerEntity.getComponent(InventoryComponent.class);
        for (EntityRef module : component.itemSlots) {
            if (module.exists() && module.getComponent(ComputerModuleComponent.class).moduleType.equals(COMPUTER_STORAGE_MODULE_TYPE)) {
                dropItemsFromComputerInternalStorage(computerEntity);
            }
        }
    }
}
