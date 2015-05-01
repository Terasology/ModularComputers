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
import org.terasology.math.Direction;
import org.terasology.world.BlockEntityRegistry;

import java.util.Map;

public class InventoryBindingMethod extends AbstractModuleMethodExecutable<Object> {
    private final String methodName;
    private BlockEntityRegistry blockEntityRegistry;
    private boolean input;

    public InventoryBindingMethod(String methodName, BlockEntityRegistry blockEntityRegistry, boolean input) {
        super(input ? "Creates the input inventory binding for the storage specified in the direction. " +
                        "This binding allows to insert items into the inventory only." : "Creates the output inventory binding for the storage specified in the direction. " +
                        "This binding allows to remove items from the inventory only.", "Inventory Binding",
                input ? "Input binding for the direction specified." : "Output binding for the direction specified.");
        this.blockEntityRegistry = blockEntityRegistry;
        this.input = input;
        this.methodName = methodName;

        addParameter("direction", "String", "Direction in which the inventory manipulator is bound to. For more information " +
                "about <h navigate:object-type-Direction>Direction</h> - read the link.");

        if (input) {
            addExample("This example creates input inventory binding to an inventory above it and prints out the slot count for it. Please make sure " +
                            "this computer has a module of Inventory Manipulator type in any of its slots.",
                    "var invBind = computer.bindModuleOfType(\"" + InventoryModuleCommonSystem.COMPUTER_INVENTORY_MODULE_TYPE + "\");\n" +
                            "var topInv = invBind.getInputInventoryBinding(\"up\");\n" +
                            "console.append(\"Inventory above has \" + invBind.getInventorySlotCount(topInv) + \" number of slots available for input.\");"
            );
        } else {
            addExample("This example creates output inventory binding to an inventory above it and prints out the slot count for it. Please make sure " +
                            "this computer has a module of Inventory Manipulator type in any of its slots.",
                    "var invBind = computer.bindModuleOfType(\"" + InventoryModuleCommonSystem.COMPUTER_INVENTORY_MODULE_TYPE + "\");\n" +
                            "var topInv = invBind.getOutputInventoryBinding(\"up\");\n" +
                            "console.append(\"Inventory above has \" + invBind.getInventorySlotCount(topInv) + \" number of slots available for output.\");"
            );
        }
    }

    @Override
    public int getCpuCycleDuration() {
        return 10;
    }

    @Override
    public Object onFunctionEnd(int line, ComputerCallback computer, Map<String, Variable> parameters, Object onFunctionStartResult) throws ExecutionException {
        Direction direction = FunctionParamValidationUtil.validateDirectionParameter(line, parameters,
                "direction", methodName);

        return new RelativeInventoryBindingCustomObject(blockEntityRegistry, direction, input);
    }
}
