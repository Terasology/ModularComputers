// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.computer.module.mobility;

import org.terasology.computer.module.DefaultComputerModule;
import org.terasology.mobileBlocks.server.BlockMoveManager;

public class MobilityComputerModule extends DefaultComputerModule {
    public MobilityComputerModule(BlockMoveManager blockMoveManager, String moduleType, String moduleName) {
        super(moduleType, moduleName);

        addMethod("move", new MoveMethod("move", blockMoveManager));
    }
}
