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
import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.EventPriority;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.logic.health.BeforeDamagedEvent;
import org.terasology.logic.inventory.InventoryComponent;
import org.terasology.logic.inventory.InventoryManager;
import org.terasology.logic.inventory.InventoryUtils;
import org.terasology.logic.inventory.PickupBuilder;
import org.terasology.logic.inventory.events.InventorySlotChangedEvent;
import org.terasology.math.geom.Vector3i;
import org.terasology.world.block.move.server.AfterBlockMovedEvent;
import org.terasology.world.block.move.server.BeforeBlockMovesEvent;
import org.terasology.world.block.move.server.BlockTransitionDuringMoveEvent;
import org.terasology.world.block.move.server.MovingBlockReplacementComponent;
import org.terasology.physics.events.ImpulseEvent;
import org.terasology.registry.In;
import org.terasology.utilities.random.FastRandom;
import org.terasology.world.BlockEntityRegistry;
import org.terasology.world.block.BlockComponent;
import org.terasology.world.block.entity.placement.PlaceBlocks;
import org.terasology.world.block.items.OnBlockToItem;

@RegisterSystem(RegisterMode.AUTHORITY)
public class StorageModuleServerSystem extends BaseComponentSystem {
    @In
    private EntityManager entityManager;
    @In
    private InventoryManager inventoryManager;
    @In
    private BlockEntityRegistry blockEntityRegistry;

    private boolean doNotMoveInventory = false;

    @ReceiveEvent
    public void computerModuleSlotChanged(InventorySlotChangedEvent event, EntityRef computerEntity, ComputerComponent computer, BlockComponent block) {
        if (!doNotMoveInventory) {
            ComputerModuleComponent oldModule = event.getOldItem().getComponent(ComputerModuleComponent.class);
            if (oldModule != null && oldModule.moduleType.equals(StorageModuleCommonSystem.COMPUTER_STORAGE_MODULE_TYPE)) {
                dropItemsFromComputerInternalStorage(computerEntity);
            }
        }

        ComputerModuleComponent newModule = event.getNewItem().getComponent(ComputerModuleComponent.class);
        if (newModule != null && newModule.moduleType.equals(StorageModuleCommonSystem.COMPUTER_STORAGE_MODULE_TYPE)) {
            EntityRef storageEntity = entityManager.create();

            InternalStorageComponent internalStorage = new InternalStorageComponent();
            internalStorage.inventoryEntity = storageEntity;

            InventoryComponent inventoryComponent = new InventoryComponent(9);
            storageEntity.addComponent(inventoryComponent);

            computerEntity.addComponent(internalStorage);
        }
    }

    @ReceiveEvent
    public void beforeComputerMoveStopDroppingInventory(BeforeBlockMovesEvent event, EntityRef entity, ComputerComponent component) {
        doNotMoveInventory = true;
    }

    @ReceiveEvent
    public void afterComputerMoveRestartDroppingInventory(AfterBlockMovedEvent event, EntityRef entity, ComputerComponent component) {
        doNotMoveInventory = false;
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
            if (module.exists() && module.getComponent(ComputerModuleComponent.class).moduleType.equals(StorageModuleCommonSystem.COMPUTER_STORAGE_MODULE_TYPE)) {
                dropItemsFromComputerInternalStorage(computerEntity);
            }
        }
    }

    @ReceiveEvent
    public void preventDestructionOfBlocksByOtherInstigators(PlaceBlocks placeBlocks, EntityRef world) {
        if (placeBlocks.getInstigator() != world) {
            for (Vector3i location : placeBlocks.getBlocks().keySet()) {
                if (blockEntityRegistry.getBlockEntityAt(location).hasComponent(MovingBlockReplacementComponent.class)) {
                    placeBlocks.consume();
                    break;
                }
            }
        }
    }

    @ReceiveEvent
    public void preventDamagingOfBlocks(BeforeDamagedEvent event, EntityRef entity, MovingBlockReplacementComponent movingBlockReplacementComponent) {
        event.consume();
    }
}
