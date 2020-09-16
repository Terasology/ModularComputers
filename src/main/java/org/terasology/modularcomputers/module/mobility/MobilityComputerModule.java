// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.modularcomputers.module.mobility;

import org.terasology.mobileBlocks.server.BlockMoveManager;
import org.terasology.modularcomputers.module.DefaultComputerModule;

public class MobilityComputerModule extends DefaultComputerModule {
    public MobilityComputerModule(BlockMoveManager blockMoveManager, String moduleType, String moduleName) {
        super(moduleType, moduleName);

        addMethod("move", new MoveMethod("move", blockMoveManager));
    }
}
