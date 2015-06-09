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

import org.terasology.computer.system.common.ComputerModuleRegistry;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.logic.config.ModuleConfigManager;
import org.terasology.logic.inventory.InventoryManager;
import org.terasology.registry.In;
import org.terasology.world.BlockEntityRegistry;
import org.terasology.world.WorldProvider;
import org.terasology.world.block.BlockManager;

@RegisterSystem(RegisterMode.ALWAYS)
public class WorldModuleCommonSystem extends BaseComponentSystem {
    public static final String WORLD_MODULE_TYPE = "World";
    @In
    private ComputerModuleRegistry computerModuleRegistry;
    @In
    private WorldProvider worldProvider;
    @In
    private BlockEntityRegistry blockEntityRegistry;
    @In
    private InventoryManager inventoryManager;
    @In
    private ModuleConfigManager moduleConfigManager;
    @In
    private BlockManager blockManager;

    @Override
    public void preBegin() {
        if (moduleConfigManager.getBooleanVariable("ModularComputers", "registerModule.world", true)) {
            computerModuleRegistry.registerComputerModule(
                    WORLD_MODULE_TYPE,
                    new WorldComputerModule(
                            worldProvider, blockEntityRegistry,
                            inventoryManager, blockManager,
                            WORLD_MODULE_TYPE, "World interaction"),
                    "This module allows to interact with objects in the world.",
                    null);
        }
    }
}
