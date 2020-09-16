// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.modularcomputers.module.storage;

import org.terasology.modularcomputers.module.DefaultComputerModule;
import org.terasology.modularcomputers.system.server.lang.ComputerModule;

import java.util.Collection;

public class StorageComputerModule extends DefaultComputerModule {
    private final String moduleType;
    private final int slotCount;

    public StorageComputerModule(String moduleType, String moduleName, int slotCount) {
        super(moduleType, moduleName);
        this.moduleType = moduleType;
        this.slotCount = slotCount;
        addMethod("getInputInventoryBinding", new StorageInventoryBindingMethod(true));
        addMethod("getOutputInventoryBinding", new StorageInventoryBindingMethod(false));
    }

    @Override
    public boolean canBePlacedInComputer(Collection<ComputerModule> computerModulesInstalled) {
        // Only one storage module can be stored in a computer
        for (ComputerModule computerModule : computerModulesInstalled) {
            if (computerModule.getModuleType().equals(moduleType)) {
                return false;
            }
        }

        return true;
    }

    public int getSlotCount() {
        return slotCount;
    }
}
