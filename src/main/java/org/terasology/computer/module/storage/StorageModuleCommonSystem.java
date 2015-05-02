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
package org.terasology.computer.module.storage;

import org.terasology.computer.module.inventory.InventoryModuleCommonSystem;
import org.terasology.computer.system.common.ComputerModuleRegistry;
import org.terasology.computer.ui.documentation.DocumentationBuilder;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.registry.In;

@RegisterSystem(RegisterMode.ALWAYS)
public class StorageModuleCommonSystem extends BaseComponentSystem {
    public static final String COMPUTER_STORAGE_MODULE_TYPE = "Storage";

    @In
    private ComputerModuleRegistry computerModuleRegistry;

    @Override
    public void preBegin() {
        String inventoryModulePageId = DocumentationBuilder.getComputerModulePageId(InventoryModuleCommonSystem.COMPUTER_INVENTORY_MODULE_TYPE);
        String inventoryModuleDumpMethodPageId = DocumentationBuilder.getComputerModuleMethodPageId(InventoryModuleCommonSystem.COMPUTER_INVENTORY_MODULE_TYPE, "dump");

        computerModuleRegistry.registerComputerModule(
                COMPUTER_STORAGE_MODULE_TYPE,
                new StorageComputerModule(COMPUTER_STORAGE_MODULE_TYPE, "Internal storage", 9),
                "This module allows storing items within the computer itself. Only one module of this type can be installed in a computer " +
                        "at a time. Player does not have access to the storage itself via user interface, however " +
                        "<h navigate:" + inventoryModulePageId + ">Inventory manipulator</h> module can be used to access it and store in an external " +
                        "storage (i.e. chest) using the <h navigate:" + inventoryModuleDumpMethodPageId + ">dump</h> method.<l>" +
                        "For more information about usage of this module - refer to <h navigate:" + inventoryModulePageId + ">Inventory manipulator</h> " +
                        "module documentation.",
                null);
    }
}
