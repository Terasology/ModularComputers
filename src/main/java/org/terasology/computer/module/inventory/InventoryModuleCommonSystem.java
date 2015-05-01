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

import org.terasology.browser.data.ParagraphData;
import org.terasology.computer.system.common.ComputerModuleRegistry;
import org.terasology.computer.ui.documentation.DocumentationBuilder;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.logic.inventory.InventoryManager;
import org.terasology.registry.In;
import org.terasology.world.BlockEntityRegistry;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

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

    @Override
    public void preBegin() {
        computerModuleRegistry.registerComputerModule(
                COMPUTER_INVENTORY_MODULE_TYPE,
                new InventoryComputerModule(inventoryModuleConditionsRegister, inventoryManager,
                        blockEntityRegistry, COMPUTER_INVENTORY_MODULE_TYPE, "Inventory manipulator"),
                "This module allows computer to manipulate inventories.",
                null);
    }

}
