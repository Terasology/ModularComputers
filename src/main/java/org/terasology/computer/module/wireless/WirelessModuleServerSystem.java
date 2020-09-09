// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.computer.module.wireless;

import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterMode;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.logic.config.ModuleConfigManager;
import org.terasology.engine.registry.In;

@RegisterSystem(RegisterMode.AUTHORITY)
public class WirelessModuleServerSystem extends BaseComponentSystem {
    @In
    private ModuleConfigManager moduleConfigManager;

    @Override
    public void preBegin() {
    }
}
