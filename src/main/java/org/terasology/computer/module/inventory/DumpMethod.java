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
import org.terasology.logic.inventory.InventoryManager;

import java.util.Map;

public class DumpMethod extends AbstractModuleMethodExecutable<Object> {
    private final String methodName;
    private InventoryManager inventoryManager;

    public DumpMethod(String methodName, InventoryManager inventoryManager) {
        super("Dumps all the items from the \"from\" inventory to the \"to\" inventory (if able).");
        this.inventoryManager = inventoryManager;
        this.methodName = methodName;

        addParameter("inventoryBindingFrom", "Inventory Binding", "Inventory it should extract items from.");
        addParameter("inventoryBindingTo", "Inventory Binding", "Inventory it should insert items to.");

        addExample("This example moves all items from an inventory above the computer to inventory to the east of the computer. Please make sure " +
                        "this computer has a module of Inventory Manipulator type in any of its slots.",
                "var invBind = computer.bindModuleOfType(\"" + InventoryModuleCommonSystem.COMPUTER_INVENTORY_MODULE_TYPE + "\");\n" +
                        "var topInv = invBind.getOutputInventoryBinding(\"up\");\n" +
                        "var eastInv = invBind.getInputInventoryBinding(\"east\");\n" +
                        "invBind.dump(topInv, eastInv);");

    }

    @Override
    public int getCpuCycleDuration() {
        return 300;
    }

    @Override
    public Object onFunctionEnd(int line, ComputerCallback computer, Map<String, Variable> parameters, Object onFunctionStartResult) throws ExecutionException {
        InventoryBinding.InventoryWithSlots inventoryFrom = FunctionParamValidationUtil.validateInventoryBinding(line, computer,
                parameters, "inventoryBindingFrom", methodName, false);
        InventoryBinding.InventoryWithSlots inventoryTo = FunctionParamValidationUtil.validateInventoryBinding(line, computer,
                parameters, "inventoryBindingTo", methodName, true);

        for (int slotNo : inventoryFrom.slots) {
            inventoryManager.moveItemToSlots(computer.getComputerEntity(), inventoryFrom.inventory, slotNo, inventoryTo.inventory, inventoryTo.slots);
        }

        return null;
    }
}
