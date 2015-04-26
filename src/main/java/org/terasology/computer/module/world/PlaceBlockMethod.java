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
package org.terasology.computer.module.world;

import com.gempukku.lang.ExecutionException;
import com.gempukku.lang.Variable;
import org.terasology.computer.FunctionParamValidationUtil;
import org.terasology.computer.context.ComputerCallback;
import org.terasology.computer.module.inventory.InventoryBinding;
import org.terasology.computer.system.server.lang.ModuleMethodExecutable;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.logic.inventory.InventoryManager;
import org.terasology.logic.inventory.InventoryUtils;
import org.terasology.math.Direction;
import org.terasology.math.Side;
import org.terasology.math.geom.Vector3f;
import org.terasology.math.geom.Vector3i;
import org.terasology.world.BlockEntityRegistry;
import org.terasology.world.WorldProvider;
import org.terasology.world.block.Block;
import org.terasology.world.block.entity.placement.PlaceBlocks;
import org.terasology.world.block.family.BlockFamily;
import org.terasology.world.block.items.BlockItemComponent;
import org.terasology.world.block.items.OnBlockItemPlaced;

import java.util.Map;

public class PlaceBlockMethod implements ModuleMethodExecutable<Object> {
    private WorldProvider worldProvider;
    private BlockEntityRegistry blockEntityRegistry;
    private InventoryManager inventoryManager;

    public PlaceBlockMethod(WorldProvider worldProvider, BlockEntityRegistry blockEntityRegistry, InventoryManager inventoryManager) {
        this.worldProvider = worldProvider;
        this.blockEntityRegistry = blockEntityRegistry;
        this.inventoryManager = inventoryManager;
    }

    @Override
    public int getCpuCycleDuration() {
        return 50;
    }

    @Override
    public int getMinimumExecutionTime() {
        return 250;
    }

    @Override
    public String[] getParameterNames() {
        return new String[]{"direction", "inventoryBinding", "slot"};
    }

    @Override
    public Object onFunctionEnd(int line, ComputerCallback computer, Map<String, Variable> parameters, Object onFunctionStartResult) throws ExecutionException {
        Direction direction = FunctionParamValidationUtil.validateDirectionParameter(line, parameters, "direction", "placeBlock");

        InventoryBinding.InventoryWithSlots inventory = FunctionParamValidationUtil.validateInventoryBinding(line, computer, parameters,
                "inventoryBinding", "placeBlock", false);

        int slotNo = FunctionParamValidationUtil.validateSlotNo(line, parameters, inventory, "slot", "itemMove");

        Vector3f computerLocation = computer.getComputerLocation();
        Vector3i directionVector = direction.getVector3i();
        Vector3i placementPos = new Vector3i(
                computerLocation.x + directionVector.x,
                computerLocation.y + directionVector.y,
                computerLocation.z + directionVector.z);

        Block blockBeforeDestroy = worldProvider.getBlock(placementPos);
        if (blockBeforeDestroy.isReplacementAllowed()) {
            Integer realSlotNo = inventory.slots.get(slotNo);
            EntityRef item = InventoryUtils.getItemAt(inventory.inventory, realSlotNo);
            BlockItemComponent blockItem = item.getComponent(BlockItemComponent.class);
            if (blockItem != null) {
                BlockFamily type = blockItem.blockFamily;

                EntityRef removedItem = inventoryManager.removeItem(inventory.inventory, computer.getComputerEntity(), realSlotNo, false, 1);
                if (removedItem != null) {
                    Side surfaceSide = Side.inDirection(direction.reverse().getVector3f());

                    Block block = type.getBlockForPlacement(worldProvider, blockEntityRegistry, placementPos, surfaceSide, null);

                    PlaceBlocks placeBlocks = new PlaceBlocks(placementPos, block, computer.getComputerEntity());
                    worldProvider.getWorldEntity().send(placeBlocks);
                    if (!placeBlocks.isConsumed()) {
                        removedItem.send(new OnBlockItemPlaced(placementPos, blockEntityRegistry.getBlockEntityAt(placementPos)));
                        removedItem.destroy();
                        return true;
                    } else {
                        inventoryManager.giveItem(inventory.inventory, computer.getComputerEntity(), removedItem, realSlotNo);
                    }
                }
            }
        }
        return false;
    }
}
