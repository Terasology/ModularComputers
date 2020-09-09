// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.computer.module.mobility;

import org.terasology.computer.system.common.ComputerModuleRegistry;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterMode;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.logic.config.ModuleConfigManager;
import org.terasology.engine.registry.In;
import org.terasology.mobileBlocks.server.BlockMoveManager;

@RegisterSystem(RegisterMode.ALWAYS)
public class MobilityModuleCommonSystem extends BaseComponentSystem {
    public static final String MOBILITY_MODULE_TYPE = "Mobility";

    @In
    private ComputerModuleRegistry computerModuleRegistry;
    @In
    private BlockMoveManager blockMoveManager;
    @In
    private ModuleConfigManager moduleConfigManager;

    @Override
    public void preBegin() {
        if (moduleConfigManager.getBooleanVariable("ModularComputers", "registerModule.mobility", true)) {
            computerModuleRegistry.registerComputerModule(
                    MOBILITY_MODULE_TYPE,
                    new MobilityComputerModule(blockMoveManager, MOBILITY_MODULE_TYPE, "Mobility"),
                    "This module allows computer to move within the world.",
                    null);
        }
    }
}