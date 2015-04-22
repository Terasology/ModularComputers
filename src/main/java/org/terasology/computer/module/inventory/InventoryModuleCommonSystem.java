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

import org.terasology.computer.system.common.ComputerModuleRegistry;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.logic.inventory.InventoryManager;
import org.terasology.registry.In;
import org.terasology.world.BlockEntityRegistry;

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
                null,
                new TreeMap<String, String>() {{
                    put("getInputInventoryBinding", "Creates the input inventory binding for the storage specified in the direction. " +
                            "This binding allows to insert items into the inventory only.");
                    put("getOutputInventoryBinding", "Creates the output inventory binding for the storage specified in the direction. " +
                            "This binding allows to remove items from the inventory only.");
                    put("getInventorySlotCount", "Queries the specified inventory to check how many slots it has available.");
                    put("getItemCount", "Checks how many items are in the specified inventory's slot.");
                    put("getItemName", "Returns the name of the item in the specified inventory's slot (if any).");
                    put("getInventoryAndChangeCondition", "Gets the information about items stored in the inventory as well as a Condition " +
                            "that allows to wait for the inventory's contents to be changed.");
                    put("itemMove", "Moves item(s) at the specified slot in the \"from\" inventory to the \"to\" inventory.");
                    put("dump", "Dumps all the items from the \"from\" inventory to the \"to\" inventory (if able).");
                }},
                new HashMap<String, Map<String, String>>() {{
                    put("getInputInventoryBinding",
                            new LinkedHashMap<String, String>() {{
                                put("direction", "[String] Direction in which the inventory manipulator is bound to. For more information " +
                                        "about <h navigate:object-type-Direction>Direction</h> - read the link.");
                            }});
                    put("getOutputInventoryBinding",
                            new LinkedHashMap<String, String>() {{
                                put("direction", "[String] Direction in which the inventory manipulator is bound to. For more information " +
                                        "about <h navigate:object-type-Direction>Direction</h> - read the link.");
                            }});
                    put("getInventorySlotCount",
                            new LinkedHashMap<String, String>() {{
                                put("inventoryBinding", "[Inventory Binding] Inventory it should query for number of slots.");
                            }});
                    put("getItemCount",
                            new LinkedHashMap<String, String>() {{
                                put("inventoryBinding", "[Inventory Binding] Inventory it should check for the amount of items.");
                                put("slot", "[Number] Slot it should check for number of items.");
                            }});
                    put("getItemName",
                            new LinkedHashMap<String, String>() {{
                                put("inventoryBinding", "[Inventory Binding] Inventory it should check for the name of item.");
                                put("slot", "[Number] Slot it should check for name of item.");
                            }});
                    put("getInventoryAndChangeCondition",
                            new LinkedHashMap<String, String>() {{
                                put("inventoryBinding", "[Inventory Binding] Inventory it should get contents and change condition for.");
                            }});
                    put("itemMove",
                            new LinkedHashMap<String, String>() {{
                                put("inventoryBindingFrom", "[Inventory Binding] Inventory it should extract the item from.");
                                put("inventoryBindingTo", "[Inventory Binding] Inventory it should insert the item to.");
                                put("slot", "[Number] Slot number of the \"from\" inventory it should extract item from.");
                            }});
                    put("dump",
                            new LinkedHashMap<String, String>() {{
                                put("inventoryBindingFrom", "[Inventory Binding] Inventory it should extract items from.");
                                put("inventoryBindingTo", "[Inventory Binding] Inventory it should insert items to.");
                            }});
                }},
                new HashMap<String, String>() {{
                    put("getInputInventoryBinding", "[Inventory Binding] Input binding for the direction specified.");
                    put("getOutputInventoryBinding", "[Inventory Binding] Output binding for the direction specified.");
                    put("getInventorySlotCount", "[Number] Number of slots in the inventory specified.");
                    put("getItemCount", "[Number] Number of items in the specified slot in the inventory.");
                    put("getItemName", "[String] Name of item in the specified slot. " +
                            "If there is an item, but the name is not known - \"Unknown\" is returned. " +
                            "If there is no item at the specified slot, a null value is returned.");
                    put("getInventoryAndChangeCondition", "[Map] Map containing to entries:\n" +
                            "- \"inventory\" - containing a List of Maps, with each entry in the list corresponding to one slot " +
                            "in the inventory, and each entry Map containing two keys - \"name\" with String value of name of items, " +
                            "as specified in the getItemName() method, and \"count\" with Number value, specifying number of items in that slot\n" +
                            "- \"condition\" - containing condition you could wait on to listen on a change of the inventory from " +
                            "the state described in the \"inventory\" key. Please note, that the condition might be fulfilled event though " +
                            "the inventory state has not changed.");
                    put("itemMove", "[Number] Number of items that was successfully moved.");
                }}, null);
    }

}
