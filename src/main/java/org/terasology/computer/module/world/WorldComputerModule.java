// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.computer.module.world;

import org.terasology.computer.module.DefaultComputerModule;
import org.terasology.module.inventory.systems.InventoryManager;
import org.terasology.engine.world.BlockEntityRegistry;
import org.terasology.engine.world.WorldProvider;
import org.terasology.engine.world.block.Block;
import org.terasology.engine.world.block.BlockManager;

public class WorldComputerModule extends DefaultComputerModule {
    public WorldComputerModule(WorldProvider worldProvider, BlockEntityRegistry blockEntityRegistry, InventoryManager inventoryManager,
                               BlockManager blockManager,
                               String moduleType, String moduleName) {
        super(moduleType, moduleName);

        Block air = blockManager.getBlock(BlockManager.AIR_ID);
        addMethod("destroyBlock", new DestroyMethod("destroyBlock", worldProvider, blockEntityRegistry, air));
        addMethod("destroyBlockToInventory", new DestroyToInventoryMethod("destroyBlockToInventory", worldProvider, blockEntityRegistry, air));
        addMethod("placeBlock", new PlaceBlockMethod("placeBlock", worldProvider, blockEntityRegistry, inventoryManager));
    }
}
