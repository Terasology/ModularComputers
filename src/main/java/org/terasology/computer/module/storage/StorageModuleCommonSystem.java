// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.computer.module.storage;

import org.terasology.computer.module.inventory.InventoryModuleCommonSystem;
import org.terasology.computer.system.common.ComputerModuleRegistry;
import org.terasology.computer.ui.documentation.DocumentationBuilder;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterMode;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.logic.config.ModuleConfigManager;
import org.terasology.engine.registry.In;

@RegisterSystem(RegisterMode.ALWAYS)
public class StorageModuleCommonSystem extends BaseComponentSystem {
    public static final String COMPUTER_STORAGE_MODULE_TYPE = "Storage";

    @In
    private ComputerModuleRegistry computerModuleRegistry;
    @In
    private ModuleConfigManager moduleConfigManager;

    @Override
    public void preBegin() {
        if (moduleConfigManager.getBooleanVariable("ModularComputers", "registerModule.storage", true)) {
            String inventoryModulePageId =
                    DocumentationBuilder.getComputerModulePageId(InventoryModuleCommonSystem.COMPUTER_INVENTORY_MODULE_TYPE);
            String inventoryModuleDumpMethodPageId =
                    DocumentationBuilder.getComputerModuleMethodPageId(InventoryModuleCommonSystem.COMPUTER_INVENTORY_MODULE_TYPE, "dump");

            computerModuleRegistry.registerComputerModule(
                    COMPUTER_STORAGE_MODULE_TYPE,
                    new StorageComputerModule(COMPUTER_STORAGE_MODULE_TYPE, "Internal storage", 9),
                    "This module allows storing items within the computer itself. Only one module of this type can be" +
                            " installed in a computer " +
                            "at a time. Player does not have access to the storage itself via user interface, however" +
                            " " +
                            "<h navigate:" + inventoryModulePageId + ">Inventory manipulator</h> module can be used " +
                            "to access it and store in an external " +
                            "storage (i.e. chest) using the <h navigate:" + inventoryModuleDumpMethodPageId + ">dump" +
                            "</h> method.",
                    null);
        }
    }
}
