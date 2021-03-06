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
