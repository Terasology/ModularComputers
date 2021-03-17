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

import org.terasology.computer.system.common.ComputerLanguageRegistry;
import org.terasology.computer.system.common.ComputerModuleRegistry;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterMode;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.logic.config.ModuleConfigManager;
import org.terasology.module.inventory.systems.InventoryManager;
import org.terasology.engine.registry.In;
import org.terasology.engine.rendering.nui.widgets.browser.data.basic.HTMLLikeParser;
import org.terasology.engine.world.BlockEntityRegistry;

import java.util.Collections;

@RegisterSystem(RegisterMode.ALWAYS)
public class InventoryModuleCommonSystem extends BaseComponentSystem {
    public static final String COMPUTER_INVENTORY_MODULE_TYPE = "Inventory";

    @In
    private ComputerModuleRegistry computerModuleRegistry;
    @In
    private BlockEntityRegistry blockEntityRegistry;
    @In
    private InventoryManager inventoryManager;
    @In
    private InventoryModuleConditionsRegister inventoryModuleConditionsRegister;
    @In
    private ComputerLanguageRegistry computerLanguageRegistry;
    @In
    private ModuleConfigManager moduleConfigManager;

    @Override
    public void preBegin() {
        if (moduleConfigManager.getBooleanVariable("ModularComputers", "registerModule.inventory", true)) {
            computerLanguageRegistry.registerObjectType(
                    "InventoryBinding",
                    Collections.singleton(HTMLLikeParser.parseHTMLLikeParagraph(null, "An object that tells a method how to access an inventory. Usually used as a parameter " +
                            "for methods in Inventory Manipulator computer module. This object comes in two types defined upon creation:<l>" +
                            "* input - that allows to place items in the specified inventory,<l>" +
                            "* output - that allows to extract items from the specified inventory.<l>" +
                            "Attempting to use an incorrect type as a parameter of a method will result in an ExecutionException.")));

            computerModuleRegistry.registerComputerModule(
                    COMPUTER_INVENTORY_MODULE_TYPE,
                    new InventoryComputerModule(inventoryModuleConditionsRegister, inventoryManager,
                            blockEntityRegistry, COMPUTER_INVENTORY_MODULE_TYPE, "Inventory manipulator"),
                    "This module allows computer to manipulate inventories.",
                    null);
        }
    }
}
