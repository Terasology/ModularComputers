// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.computer.module.inventory;

import org.terasology.computer.module.DefaultComputerModule;
import org.terasology.module.inventory.systems.InventoryManager;
import org.terasology.engine.world.BlockEntityRegistry;

public class InventoryComputerModule extends DefaultComputerModule {
    public InventoryComputerModule(InventoryModuleConditionsRegister inventoryModuleConditionsRegister,
                                   InventoryManager inventoryManager,
                                   BlockEntityRegistry blockEntityRegistry, String moduleType, String moduleName) {
        super(moduleType, moduleName);

        addMethod("getInputInventoryBinding", new InventoryBindingMethod("getInputInventoryBinding", blockEntityRegistry, true));
        addMethod("getOutputInventoryBinding", new InventoryBindingMethod("getOutputInventoryBinding", blockEntityRegistry, false));
        addMethod("getInventorySlotCount", new InventorySlotCountMethod("getInventorySlotCount"));
        addMethod("getItemCount", new ItemCountMethod("getItemCount"));
        addMethod("getItemName", new ItemNameMethod("getItemName"));
        addMethod("getInventoryAndChangeCondition", new InventoryAndChangeConditionMethod(
                "getInventoryAndChangeCondition", inventoryModuleConditionsRegister));
        addMethod("itemMode", new ItemMoveMethod("itemMove", inventoryManager));
        addMethod("dump", new DumpMethod("dump", inventoryManager));
    }
}
