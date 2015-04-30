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
package org.terasology.computer.module.world;

import org.terasology.browser.data.ParagraphData;
import org.terasology.computer.module.inventory.InventoryModuleCommonSystem;
import org.terasology.computer.system.common.ComputerModuleRegistry;
import org.terasology.computer.ui.documentation.DocumentationBuilder;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.logic.inventory.InventoryManager;
import org.terasology.registry.In;
import org.terasology.world.BlockEntityRegistry;
import org.terasology.world.WorldProvider;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

@RegisterSystem(RegisterMode.ALWAYS)
public class WorldModuleCommonSystem extends BaseComponentSystem {
    private static final String WORLD_MODULE_TYPE = "World";
    @In
    private ComputerModuleRegistry computerModuleRegistry;
    @In
    private WorldProvider worldProvider;
    @In
    private BlockEntityRegistry blockEntityRegistry;
    @In
    private InventoryManager inventoryManager;

    @Override
    public void preBegin() {
        computerModuleRegistry.registerComputerModule(
                WORLD_MODULE_TYPE,
                new WorldComputerModule(
                        worldProvider, blockEntityRegistry,
                        inventoryManager, WORLD_MODULE_TYPE, "World interaction"),
                "This module allows to interact with objects in the world.",
                null,
                new TreeMap<String, String>() {{
                    put("destroyBlock", "Destroys the block in the specified direction. The resulting items from destroying the " +
                            "block are scattered on the ground.");
                    put("destroyBlockToInventory", "Destroys the block in the specified direction. The resulting items from " +
                            "destroying the block are added to the inventory specified. If inventory is unable to accept those " +
                            "items, the are scattered on the ground.");
                    put("placeBlock", "Places block from inventory in the specified direction.");
                }},
                new HashMap<String, Map<String, String>>() {{
                    put("destroyBlock",
                            new LinkedHashMap<String, String>() {{
                                put("direction", "[String] Direction in which to destroy the block. For more information " +
                                        "about <h navigate:object-type-Direction>Direction</h> - read the link.");
                            }});
                    put("destroyBlockToInventory",
                            new LinkedHashMap<String, String>() {{
                                put("direction", "[String] Direction in which to destroy the block. For more information " +
                                        "about <h navigate:object-type-Direction>Direction</h> - read the link.");
                                put("inventoryBinding", "[Inventory Binding] Inventory to which store the items, please note " +
                                        "that this Inventory Binding has to be of the input type.");
                            }});
                    put("placeBlock",
                            new LinkedHashMap<String, String>() {{
                                put("direction", "[String] Direction in which to place the block. For more information " +
                                        "about <h navigate:object-type-Direction>Direction</h> - read the link.");
                                put("inventoryBinding", "[Inventory Binding] Inventory from which to place the block, please " +
                                        "note that this Inventory Binding has to be of the output type.");
                                put("slot", "[Number] Slot number to take block from for placement.");
                            }});
                }},
                new HashMap<String, String>() {{
                    put("destroyBlock", "[Boolean] Whether destroying the specified block was successful.");
                    put("destroyBlockToInventory", "[Boolean] Whether destroying the specified block was successful.");
                    put("placeBlock", "[Boolean] Whether placement of the block was successful.");
                }},
                new HashMap<String, Collection<ParagraphData>>() {{
                    put("destroyBlock", DocumentationBuilder.createExampleParagraphs(
                            "This example destroys the block below the computer. Please make sure " +
                                    "this computer has a module of World Interaction type in any of its slots.",
                            "var worldMod = computer.bindModuleOfType(\"" + WORLD_MODULE_TYPE + "\");\n" +
                                    "worldMod.destroyBlock(\"down\");"
                    ));
                    put("destroyBlockToInventory", DocumentationBuilder.createExampleParagraphs(
                            "This example destroys the block below the computer and places the resulting items in inventory " +
                                    "above it. Please make sure this computer has a modules of World Interaction type " +
                                    "and Inventory Manipulator in any of its slots.",
                            "var worldMod = computer.bindModuleOfType(\"" + WORLD_MODULE_TYPE + "\");\n" +
                                    "var inventoryMod = computer.bindModuleOfType(\"" + InventoryModuleCommonSystem.COMPUTER_INVENTORY_MODULE_TYPE + "\");\n" +
                                    "var upBinding = inventoryMod.getInputInventoryBinding(\"up\");\n" +
                                    "worldMod.destroyBlockToInventory(\"down\", upBinding);"
                    ));
                    put("placeBlock", DocumentationBuilder.createExampleParagraphs(
                            "This example places a block below it, the block is coming from first slot of inventory above it. Please make sure " +
                                    "this computer has a modules of World Interaction type and Inventory Manipulator in any of its slots.",
                            "var worldMod = computer.bindModuleOfType(\"" + WORLD_MODULE_TYPE + "\");\n" +
                                    "var inventoryMod = computer.bindModuleOfType(\"" + InventoryModuleCommonSystem.COMPUTER_INVENTORY_MODULE_TYPE + "\");\n" +
                                    "var upBinding = inventoryMod.getOutputInventoryBinding(\"up\");\n" +
                                    "worldMod.placeBlock(\"down\", upBinding, 0);"
                    ));
                }});
    }
}
