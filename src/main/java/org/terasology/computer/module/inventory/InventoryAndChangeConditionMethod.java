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

import com.gempukku.lang.ExecutionException;
import com.gempukku.lang.Variable;
import org.terasology.computer.FunctionParamValidationUtil;
import org.terasology.computer.context.ComputerCallback;
import org.terasology.computer.system.server.lang.AbstractModuleMethodExecutable;
import org.terasology.computer.system.server.lang.os.condition.InventoryCondition;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.module.inventory.systems.InventoryUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InventoryAndChangeConditionMethod extends AbstractModuleMethodExecutable<Object> {
    private final String methodName;
    private InventoryModuleConditionsRegister inventoryModuleConditionsRegister;

    public InventoryAndChangeConditionMethod(String methodName, InventoryModuleConditionsRegister inventoryModuleConditionsRegister) {
        super("Gets the information about items stored in the inventory as well as a Condition " +
                        "that allows to wait for the inventory's contents to be changed.", "Map",
                "Map containing to entries:<l>" +
                        "- \"inventory\" - containing a List of Maps, with each entry in the list corresponding to one slot " +
                        "in the inventory, and each entry Map containing two keys - \"name\" with String value of name of items, " +
                        "as specified in the getItemName() method, and \"count\" with Number value, specifying number of items in that slot<l>" +
                        "- \"condition\" - containing condition you could wait on to listen on a change of the inventory from " +
                        "the state described in the \"inventory\" key. Please note, that the condition might be fulfilled even though " +
                        "the inventory state has not changed.");
        this.inventoryModuleConditionsRegister = inventoryModuleConditionsRegister;
        this.methodName = methodName;

        addParameter("inventoryBinding", "InventoryBinding", "Inventory it should get contents and change condition for.");

        addExample(
                "This example prints out the contents of the output inventory above the computer to the console on each change of the inventory contents. Please make sure " +
                        "this computer has a module of Inventory Manipulator type in any of its slots.",
                "var invBind = computer.bindModuleOfType(\"" + InventoryModuleCommonSystem.COMPUTER_INVENTORY_MODULE_TYPE + "\");\n" +
                        "var topInv = invBind.getOutputInventoryBinding(\"up\");\n" +
                        "while (true) {\n" +
                        "  var inventoryAndCondition = invBind.getInventoryAndChangeCondition(topInv);\n" +
                        "  var inventory = inventoryAndCondition[\"inventory\"];\n" +
                        "  var condition = inventoryAndCondition[\"condition\"];\n" +
                        "  var inventorySize = inventory.size();\n" +
                        "  for (var i=0; i<inventorySize; i++) {\n" +
                        "    var contents = inventory[i];\n" +
                        "    var name = contents[\"name\"];\n" +
                        "    var count = contents[\"count\"];\n" +
                        "    console.append(\"Slot \"+(i+1)+\" has \"+count+\" of \"+name);\n" +
                        "  }\n" +
                        "  os.waitFor(condition);\n" +
                        "}"
        );
    }

    @Override
    public int getCpuCycleDuration() {
        return 200;
    }

    @Override
    public Object onFunctionEnd(int line, ComputerCallback computer, Map<String, Variable> parameters, Object onFunctionStartResult) throws ExecutionException {
        InventoryBinding.InventoryWithSlots inventory = FunctionParamValidationUtil.validateInventoryBinding(line, computer,
                parameters, "inventoryBinding", methodName, null);

        Map<String, Variable> result = new HashMap<>();

        List<Variable> inventoryResult = getInventory(inventory);
        final List<Variable> inventoryCopyResult = getInventory(inventory);

        result.put("inventory", new Variable(inventoryResult));

        result.put("condition", new Variable(
                new InventoryCondition() {
                    @Override
                    public boolean checkRelease() {
                        try {
                            InventoryBinding.InventoryWithSlots inventory = FunctionParamValidationUtil.validateInventoryBinding(line, computer,
                                    parameters, "inventoryBinding", methodName, null);
                            List<Variable> currentInventory = getInventory(inventory);
                            if (!currentInventory.equals(inventoryCopyResult)) {
                                release(null);
                                return true;
                            } else {
                                return false;
                            }
                        } catch (ExecutionException exp) {
                            releaseWithError(exp);
                            return true;
                        }
                    }

                    @Override
                    public void cancelCondition() {
                        releaseWithError(new ExecutionException(line, "Observed inventory has been removed."));
                    }

                    @Override
                    protected Runnable registerAwaitingCondition() throws ExecutionException {
                        if (!checkRelease()) {
                            inventoryModuleConditionsRegister.addInventoryChangeListener(inventory.inventory, this);
                            final InventoryCondition condition = this;
                            return new Runnable() {
                                @Override
                                public void run() {
                                    inventoryModuleConditionsRegister.removeInventoryChangeListener(inventory.inventory, condition);
                                }
                            };
                        } else {
                            return null;
                        }
                    }
                }));

        return result;
    }

    private List<Variable> getInventory(InventoryBinding.InventoryWithSlots inventory) {
        List<Variable> inventoryResult = new ArrayList<>();

        for (int slot : inventory.slots) {
            EntityRef item = InventoryUtils.getItemAt(inventory.inventory, slot);
            Map<String, Variable> itemMap = new HashMap<>();

            int itemCount = InventoryModuleUtils.getItemCount(item);
            String itemName = InventoryModuleUtils.getItemName(item);

            itemMap.put("name", new Variable(itemName));
            itemMap.put("count", new Variable(itemCount));

            inventoryResult.add(new Variable(itemMap));
        }
        return inventoryResult;
    }
}
