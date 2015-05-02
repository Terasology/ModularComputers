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

import java.util.Map;

public class InventorySlotCountMethod extends AbstractModuleMethodExecutable<Object> {

    private final String methodName;

    public InventorySlotCountMethod(String methodName) {
        super("Queries the specified inventory to check how many slots it has available.", "Number",
                "Number of slots in the inventory specified.");
        this.methodName = methodName;

        addParameter("inventoryBinding", "InventoryBinding", "Inventory it should query for number of slots.");

        addExample("This example creates output inventory binding to an inventory above it and prints out the slot count for it. Please make sure " +
                        "this computer has a module of Inventory Manipulator type in any of its slots.",
                "var invBind = computer.bindModuleOfType(\"" + InventoryModuleCommonSystem.COMPUTER_INVENTORY_MODULE_TYPE + "\");\n" +
                        "var topInv = invBind.getOutputInventoryBinding(\"up\");\n" +
                        "console.append(\"Inventory above has \" + invBind.getInventorySlotCount(topInv) + \" number of slots available for output.\");"
        );
    }

    @Override
    public int getCpuCycleDuration() {
        return 50;
    }

    @Override
    public Object onFunctionEnd(int line, ComputerCallback computer, Map<String, Variable> parameters, Object onFunctionStartResult) throws ExecutionException {
        InventoryBinding.InventoryWithSlots inventory = FunctionParamValidationUtil.validateInventoryBinding(line, computer,
                parameters, "inventoryBinding", methodName, null);

        return inventory.slots.size();
    }
}
