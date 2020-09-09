// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.computer.module.world;

import org.terasology.computer.system.common.ComputerModuleRegistry;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterMode;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.logic.config.ModuleConfigManager;
import org.terasology.engine.registry.In;
import org.terasology.engine.world.BlockEntityRegistry;
import org.terasology.engine.world.WorldProvider;
import org.terasology.engine.world.block.BlockManager;
import org.terasology.inventory.logic.InventoryManager;

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
