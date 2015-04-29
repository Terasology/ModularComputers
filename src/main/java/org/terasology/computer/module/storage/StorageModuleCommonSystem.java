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

import org.terasology.browser.data.ParagraphData;
import org.terasology.computer.module.inventory.InventoryModuleCommonSystem;
import org.terasology.computer.system.common.ComputerModuleRegistry;
import org.terasology.computer.ui.documentation.DocumentationBuilder;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.registry.In;

import java.util.Collection;
import java.util.HashMap;
import java.util.TreeMap;

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
                null,
                new TreeMap<String, String>() {{
                    put("getInputInventoryBinding", "Creates the input inventory binding for the Internal storage.");
                    put("getOutputInventoryBinding", "Creates the output inventory binding for the Internal storage.");
                }},
                new HashMap<>(),
                new HashMap<String, String>() {{
                    put("getInputInventoryBinding", "[Inventory Binding] Returns inventory binding allowing to put items into this Internal storage.");
                    put("getOutputInventoryBinding", "[Inventory Binding] Returns inventory binding allowing to extract items from this Internal storage.");
                }},
                new HashMap<String, Collection<ParagraphData>>() {{
                    put("getInputInventoryBinding", DocumentationBuilder.createExampleParagraphs(
                            "This example creates input and output inventory binding for the internal storage and " +
                                    "an input and output inventory binding for storage above the computer, then dumps " +
                                    "the contents of the inventory above into the internal storage, waits for 5 seconds " +
                                    "and dumps the items back into the inventory above.  Please make sure " +
                                    "this computer has a module of Inventory Manipulator type in any of its slots, as well " +
                                    "as Internal Storage module.",
                            "var storageMod = computer.bindModuleOfType(\"" + COMPUTER_STORAGE_MODULE_TYPE + "\");\n" +
                                    "var inventoryMod = computer.bindModuleOfType(\"" + InventoryModuleCommonSystem.COMPUTER_INVENTORY_MODULE_TYPE + "\");\n" +
                                    "var inputInternal = storageMod.getInputInventoryBinding();\n" +
                                    "var outputInternal = storageMod.getOutputInventoryBinding();\n" +
                                    "var inputUp = inventoryMod.getInputInventoryBinding(\"up\");\n" +
                                    "var outputUp = inventoryMod.getOutputInventoryBinding(\"up\");\n" +
                                    "\n" +
                                    "inventoryMod.dump(outputUp, inputInternal);\n" +
                                    "os.waitFor(os.createSleepMs(5000));\n" +
                                    "inventoryMod.dump(outputInternal, inputUp);"
                    ));
                    put("getOutputInventoryBinding", DocumentationBuilder.createExampleParagraphs(
                            "This example creates input and output inventory binding for the internal storage and " +
                                    "an input and output inventory binding for storage above the computer, then dumps " +
                                    "the contents of the inventory above into the internal storage, waits for 5 seconds " +
                                    "and dumps the items back into the inventory above.  Please make sure " +
                                    "this computer has a module of Inventory Manipulator type in any of its slots, as well " +
                                    "as Internal Storage module.",
                            "var storageMod = computer.bindModuleOfType(\"" + COMPUTER_STORAGE_MODULE_TYPE + "\");\n" +
                                    "var inventoryMod = computer.bindModuleOfType(\"" + InventoryModuleCommonSystem.COMPUTER_INVENTORY_MODULE_TYPE + "\");\n" +
                                    "var inputInternal = storageMod.getInputInventoryBinding();\n" +
                                    "var outputInternal = storageMod.getOutputInventoryBinding();\n" +
                                    "var inputUp = inventoryMod.getInputInventoryBinding(\"up\");\n" +
                                    "var outputUp = inventoryMod.getOutputInventoryBinding(\"up\");\n" +
                                    "\n" +
                                    "inventoryMod.dump(outputUp, inputInternal);\n" +
                                    "os.waitFor(os.createSleepMs(5000));\n" +
                                    "inventoryMod.dump(outputInternal, inputUp);"
                    ));
                }}
        );
    }
}
