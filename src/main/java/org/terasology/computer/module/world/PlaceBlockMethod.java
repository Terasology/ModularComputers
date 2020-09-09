// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.computer.module.world;

import com.gempukku.lang.ExecutionException;
import com.gempukku.lang.Variable;
import org.terasology.computer.FunctionParamValidationUtil;
import org.terasology.computer.context.ComputerCallback;
import org.terasology.computer.module.inventory.InventoryBinding;
import org.terasology.computer.module.inventory.InventoryModuleCommonSystem;
import org.terasology.computer.system.server.lang.AbstractModuleMethodExecutable;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.math.Direction;
import org.terasology.engine.math.Side;
import org.terasology.engine.world.BlockEntityRegistry;
import org.terasology.engine.world.WorldProvider;
import org.terasology.engine.world.block.Block;
import org.terasology.engine.world.block.entity.placement.PlaceBlocks;
import org.terasology.engine.world.block.family.BlockFamily;
import org.terasology.engine.world.block.items.BlockItemComponent;
import org.terasology.engine.world.block.items.OnBlockItemPlaced;
import org.terasology.inventory.logic.InventoryManager;
import org.terasology.inventory.logic.InventoryUtils;
import org.terasology.math.geom.Vector3f;
import org.terasology.math.geom.Vector3i;

import java.util.Map;

public class PlaceBlockMethod extends AbstractModuleMethodExecutable<Object> {
    private final String methodName;
    private final WorldProvider worldProvider;
    private final BlockEntityRegistry blockEntityRegistry;
    private final InventoryManager inventoryManager;

    public PlaceBlockMethod(String methodName, WorldProvider worldProvider, BlockEntityRegistry blockEntityRegistry,
                            InventoryManager inventoryManager) {
        super("Places block from inventory in the specified direction.", "Boolean", "Whether placement of the block " +
                "was successful.");
        this.worldProvider = worldProvider;
        this.blockEntityRegistry = blockEntityRegistry;
        this.inventoryManager = inventoryManager;
        this.methodName = methodName;

        addParameter("direction", "Direction", "Direction in which to place the block.");
        addParameter("inventoryBinding", "InventoryBinding", "Inventory from which to place the block, please " +
                "note that this Inventory Binding has to be of the output type.");
        addParameter("slot", "Number", "Slot number to take block from for placement.");

        addExample(
                "This example places a block below it, the block is coming from first slot of inventory above it. " +
                        "Please make sure " +
                        "this computer has a modules of World Interaction type and Inventory Manipulator in any of " +
                        "its slots.",
                "var worldMod = computer.bindModuleOfType(\"" + WorldModuleCommonSystem.WORLD_MODULE_TYPE + "\");\n" +
                        "var inventoryMod = computer.bindModuleOfType(\"" + InventoryModuleCommonSystem.COMPUTER_INVENTORY_MODULE_TYPE + "\");\n" +
                        "var upBinding = inventoryMod.getOutputInventoryBinding(\"up\");\n" +
                        "worldMod.placeBlock(\"down\", upBinding, 0);");
    }

    @Override
    public int getCpuCycleDuration() {
        return 50;
    }

    @Override
    public int getMinimumExecutionTime(int line, ComputerCallback computer, Map<String, Variable> parameters) throws ExecutionException {
        return 250;
    }

    @Override
    public Object onFunctionEnd(int line, ComputerCallback computer, Map<String, Variable> parameters,
                                Object onFunctionStartResult) throws ExecutionException {
        Direction direction = FunctionParamValidationUtil.validateDirectionParameter(line, parameters, "direction",
                methodName);

        InventoryBinding.InventoryWithSlots inventory = FunctionParamValidationUtil.validateInventoryBinding(line,
                computer, parameters,
                "inventoryBinding", methodName, false);

        int slotNo = FunctionParamValidationUtil.validateSlotNo(line, parameters, inventory, "slot", methodName);

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

                EntityRef removedItem = inventoryManager.removeItem(inventory.inventory, computer.getComputerEntity()
                        , realSlotNo, false, 1);
                if (removedItem != null) {
                    Side surfaceSide = Side.inDirection(direction.reverse().getVector3f());

                    Block block = type.getBlockForPlacement(placementPos, surfaceSide, null);

                    PlaceBlocks placeBlocks = new PlaceBlocks(placementPos, block, computer.getComputerEntity());
                    worldProvider.getWorldEntity().send(placeBlocks);
                    if (!placeBlocks.isConsumed()) {
                        removedItem.send(new OnBlockItemPlaced(placementPos,
                                blockEntityRegistry.getBlockEntityAt(placementPos)));
                        removedItem.destroy();
                        return true;
                    } else {
                        inventoryManager.giveItem(inventory.inventory, computer.getComputerEntity(), removedItem,
                                realSlotNo);
                    }
                }
            }
        }
        return false;
    }
}
