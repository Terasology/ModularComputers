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
package org.terasology.computer.module.inventory;

import org.terasology.computer.module.DefaultComputerModule;
import org.terasology.logic.inventory.InventoryManager;
import org.terasology.world.BlockEntityRegistry;

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
        addMethod("getInventoryAndChangeCondition", new InventoryAndChangeConditionMethod("getInventoryAndChangeCondition", inventoryModuleConditionsRegister));
        addMethod("itemMode", new ItemMoveMethod("itemMove", inventoryManager));
        addMethod("dump", new DumpMethod("dump", inventoryManager));
    }
}
