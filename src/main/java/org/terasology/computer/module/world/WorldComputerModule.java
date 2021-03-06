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
