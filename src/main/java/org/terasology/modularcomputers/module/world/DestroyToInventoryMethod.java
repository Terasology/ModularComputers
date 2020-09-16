// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.modularcomputers.module.world;

import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.logic.destruction.DestroyEvent;
import org.terasology.engine.math.Direction;
import org.terasology.engine.utilities.Assets;
import org.terasology.engine.world.BlockEntityRegistry;
import org.terasology.engine.world.WorldProvider;
import org.terasology.engine.world.block.Block;
import org.terasology.math.geom.Vector3f;
import org.terasology.math.geom.Vector3i;
import org.terasology.modularcomputers.FunctionParamValidationUtil;
import org.terasology.modularcomputers.context.ComputerCallback;
import org.terasology.modularcomputers.module.inventory.InventoryBinding;
import org.terasology.modularcomputers.module.inventory.InventoryModuleCommonSystem;
import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.ExecutionException;
import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.Variable;
import org.terasology.modularcomputers.system.server.lang.AbstractModuleMethodExecutable;

import java.util.Map;

public class DestroyToInventoryMethod extends AbstractModuleMethodExecutable<Object> {
    private final String methodName;
    private final Block replaceBlock;
    private final WorldProvider worldProvider;
    private final BlockEntityRegistry blockEntityRegistry;

    public DestroyToInventoryMethod(String methodName, WorldProvider worldProvider,
                                    BlockEntityRegistry blockEntityRegistry,
                                    Block replaceBlock) {
        super("Destroys the block in the specified direction. The resulting items from " +
                "destroying the block are added to the inventory specified. If inventory is unable to accept those " +
                "items, the are scattered on the ground.", "Boolean", "Whether destroying the specified block was " +
                "successful.");
        this.worldProvider = worldProvider;
        this.blockEntityRegistry = blockEntityRegistry;
        this.methodName = methodName;
        this.replaceBlock = replaceBlock;

        addParameter("direction", "Direction", "Direction in which to destroy the block.");
        addParameter("inventoryBinding", "InventoryBinding", "Inventory to which store the items, please note " +
                "that this Inventory Binding has to be of the input type.");

        addExample("This example destroys the block below the computer and places the resulting items in inventory " +
                        "above it. Please make sure this computer has a modules of World Interaction type " +
                        "and Inventory Manipulator in any of its slots.",
                "var worldMod = computer.bindModuleOfType(\"" + WorldModuleCommonSystem.WORLD_MODULE_TYPE + "\");\n" +
                        "var inventoryMod = computer.bindModuleOfType(\"" + InventoryModuleCommonSystem.COMPUTER_INVENTORY_MODULE_TYPE + "\");\n" +
                        "var upBinding = inventoryMod.getInputInventoryBinding(\"up\");\n" +
                        "worldMod.destroyBlockToInventory(\"down\", upBinding);"
        );
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
                "inventoryBinding", methodName, true);

        Vector3f computerLocation = computer.getComputerLocation();
        Vector3i directionVector = direction.getVector3i();
        Vector3i harvestLocation = new Vector3i(
                computerLocation.x + directionVector.x,
                computerLocation.y + directionVector.y,
                computerLocation.z + directionVector.z);

        Block blockBeforeDestroy = worldProvider.getBlock(harvestLocation);
        if (blockBeforeDestroy != replaceBlock) {
            EntityRef harvestedEntity = blockEntityRegistry.getBlockEntityAt(harvestLocation);
            harvestedEntity.send(new DestroyEvent(inventory.inventory, computer.getComputerEntity(),
                    Assets.getPrefab("ModularComputers:harvestDamagePickup").get()));

            return worldProvider.getBlock(harvestLocation) != blockBeforeDestroy;
        } else {
            return true;
        }
    }
}
