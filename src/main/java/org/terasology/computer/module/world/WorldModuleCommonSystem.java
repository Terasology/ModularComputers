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

import org.terasology.computer.system.common.ComputerModuleRegistry;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.registry.In;
import org.terasology.world.BlockEntityRegistry;
import org.terasology.world.WorldProvider;

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

    @Override
    public void preBegin() {
        computerModuleRegistry.registerComputerModule(
                WORLD_MODULE_TYPE,
                new WorldComputerModule(
                        worldProvider, blockEntityRegistry,
                        WORLD_MODULE_TYPE, "World interaction"),
                "This module allows to interact with objects in the world.",
                new TreeMap<String, String>() {{
                    put("destroyBlock", "Destroys the block in the specified direction. The resulting items from destroying the " +
                            "block are scattered on the ground.");
                    put("destroyBlockToInventory", "Destroys the block in the specified direction. The resulting items from " +
                            "destroying the block are added to the inventory specified. If inventory is unable to accept those " +
                            "items, the are scattered on the ground.");
                }},
                new HashMap<String, Map<String, String>>() {{
                    put("destroyBlock",
                            new LinkedHashMap<String, String>() {{
                                put("direction", "[String] Direction in which to destroy the block. For more information " +
                                        "about <h navigate:object-type-Direction>Direction</h> - read the link.");
                            }});
                    put("destroyBlockToInventory",
                            new LinkedHashMap<String, String>() {{
                                put("direction", "[String] Direction in which to destroy the block.  For more information " +
                                        "about <h navigate:object-type-Direction>Direction</h> - read the link.");
                                put("inventoryBinding", "[Inventory Binding] Inventory to which store the items, please note " +
                                        "that this Inventory Binding has to be of the input type.");
                            }});
                }},
                new HashMap<String, String>() {{
                    put("destroyBlock", "[Boolean] Whether destroying the specified block was successful.");
                    put("destroyBlockToInventory", "[Boolean] Whether destroying the specified block was successful.");
                }});
    }
}
