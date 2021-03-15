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

import org.joml.Vector3f;
import org.joml.Vector3i;
import org.terasology.computer.component.ComputerComponent;
import org.terasology.computer.component.ComputerModuleComponent;
import org.terasology.computer.system.common.ComputerModuleRegistry;
import org.terasology.engine.entitySystem.entity.EntityManager;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.event.EventPriority;
import org.terasology.engine.entitySystem.event.ReceiveEvent;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterMode;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.module.inventory.components.InventoryComponent;
import org.terasology.module.inventory.systems.InventoryManager;
import org.terasology.module.inventory.systems.InventoryUtils;
import org.terasology.engine.logic.inventory.events.DropItemEvent;
import org.terasology.module.inventory.events.InventorySlotChangedEvent;
import org.terasology.engine.physics.events.ImpulseEvent;
import org.terasology.engine.registry.In;
import org.terasology.engine.utilities.random.FastRandom;
import org.terasology.engine.world.BlockEntityRegistry;
import org.terasology.engine.world.block.BlockComponent;
import org.terasology.engine.world.block.items.OnBlockToItem;
import org.terasology.mobileBlocks.server.AfterBlockMovedEvent;
import org.terasology.mobileBlocks.server.BeforeBlockMovesEvent;
import org.terasology.mobileBlocks.server.BlockTransitionDuringMoveEvent;

@RegisterSystem(RegisterMode.AUTHORITY)
public class StorageModuleServerSystem extends BaseComponentSystem {
    @In
    private ComputerModuleRegistry computerModuleRegistry;
    @In
    private EntityManager entityManager;
    @In
    private InventoryManager inventoryManager;
    @In
    private BlockEntityRegistry blockEntityRegistry;

    private boolean computerIsMoving;

    @ReceiveEvent
    public void computerModuleSlotChanged(InventorySlotChangedEvent event, EntityRef computerEntity, ComputerComponent computer) {
        // If computer is moving, we have to preserve the component to be able to copy it's data, so do not remove the
        // component at this point, the entity will be destroyed upon movement finish anyway
        if (!computerIsMoving) {
            ComputerModuleComponent oldModule = event.getOldItem().getComponent(ComputerModuleComponent.class);
            if (oldModule != null && oldModule.moduleType.equals(StorageModuleCommonSystem.COMPUTER_STORAGE_MODULE_TYPE)) {
                EntityRef inventoryEntity = computerEntity.getComponent(InternalStorageComponent.class).inventoryEntity;

                dropItemsFromComputerInternalStorage(computerEntity, inventoryEntity);
                inventoryEntity.destroy();

                computerEntity.removeComponent(InternalStorageComponent.class);
            }
        }

        ComputerModuleComponent newModule = event.getNewItem().getComponent(ComputerModuleComponent.class);
        if (newModule != null && newModule.moduleType.equals(StorageModuleCommonSystem.COMPUTER_STORAGE_MODULE_TYPE)) {
            StorageComputerModule computerModuleByType = (StorageComputerModule) computerModuleRegistry.getComputerModuleByType(newModule.moduleType);

            EntityRef storageEntity = entityManager.create();

            InternalStorageComponent internalStorage = new InternalStorageComponent();
            internalStorage.inventoryEntity = storageEntity;

            InventoryComponent inventoryComponent = new InventoryComponent(computerModuleByType.getSlotCount());
            storageEntity.addComponent(inventoryComponent);

            computerEntity.addComponent(internalStorage);
        }
    }

    @ReceiveEvent
    public void beforeComputerMoveSetFlag(BeforeBlockMovesEvent event, EntityRef entity, ComputerComponent component) {
        computerIsMoving = true;
    }

    @ReceiveEvent
    public void afterComputerMoveResetFlag(AfterBlockMovedEvent event, EntityRef entity, ComputerComponent component) {
        computerIsMoving = false;
    }

    @ReceiveEvent(priority = EventPriority.PRIORITY_TRIVIAL)
    public void computerMovedCopyInternalStorage(BlockTransitionDuringMoveEvent event, EntityRef entity, InternalStorageComponent storage) {
        EntityRef inventoryEntity = storage.inventoryEntity;
        EntityRef newInventoryEntity = event.getIntoEntity().getComponent(InternalStorageComponent.class).inventoryEntity;

        int slotCount = InventoryUtils.getSlotCount(inventoryEntity);
        // We assume the number of slots does not change
        for (int i = 0; i < slotCount; i++) {
            int stackCount = InventoryUtils.getStackCount(InventoryUtils.getItemAt(inventoryEntity, i));
            inventoryManager.moveItem(inventoryEntity, null, i, newInventoryEntity, i, stackCount);
        }
    }

    private void dropItemsFromComputerInternalStorage(EntityRef computerEntity, EntityRef inventoryEntity) {
        if (computerEntity.hasComponent(BlockComponent.class)) {
            Vector3i blockLocation = computerEntity.getComponent(BlockComponent.class).getPosition(new Vector3i());

            InventoryComponent inventoryComponent = inventoryEntity.getComponent(InventoryComponent.class);

            FastRandom random = new FastRandom();
            for (EntityRef itemSlot : inventoryComponent.itemSlots) {
                if (itemSlot.exists()) {
                    itemSlot.send(new DropItemEvent(new Vector3f(blockLocation)));
                    itemSlot.send(new ImpulseEvent(random.nextVector3f(30.0f, new Vector3f())));
                }
            }
        }
    }

    @ReceiveEvent(priority = EventPriority.PRIORITY_TRIVIAL)
    public void computerDestroyed(OnBlockToItem event, EntityRef computerEntity, ComputerComponent computer) {
        InventoryComponent component = computerEntity.getComponent(InventoryComponent.class);
        for (EntityRef module : component.itemSlots) {
            if (module.exists() && module.getComponent(ComputerModuleComponent.class).moduleType.equals(StorageModuleCommonSystem.COMPUTER_STORAGE_MODULE_TYPE)) {
                EntityRef inventoryEntity = computerEntity.getComponent(InternalStorageComponent.class).inventoryEntity;
                dropItemsFromComputerInternalStorage(computerEntity, inventoryEntity);
                inventoryEntity.destroy();
            }
        }
    }
}
