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

import com.gempukku.lang.ExecutionException;
import com.gempukku.lang.Variable;
import org.terasology.computer.context.ComputerCallback;
import org.terasology.computer.module.inventory.InventoryModuleCommonSystem;
import org.terasology.computer.system.server.lang.AbstractModuleMethodExecutable;

import java.util.Map;

public class StorageInventoryBindingMethod extends AbstractModuleMethodExecutable<Object> {
    private boolean input;

    public StorageInventoryBindingMethod(boolean input) {
        super(input ? "Creates the input inventory binding for the Internal storage." : "Creates the output inventory binding for the Internal storage.",
                "InventoryBinding",
                input ? "Returns inventory binding allowing to put items into this Internal storage." : "Returns inventory binding allowing to extract items from this Internal storage.");
        this.input = input;

        addExample(
                "This example creates input and output inventory binding for the internal storage and " +
                        "an input and output inventory binding for storage above the computer, then dumps " +
                        "the contents of the inventory above into the internal storage, waits for 5 seconds " +
                        "and dumps the items back into the inventory above. Please make sure " +
                        "this computer has a module of Inventory Manipulator type in any of its slots, as well " +
                        "as Internal Storage module.",
                "var storageMod = computer.bindModuleOfType(\"" + StorageModuleCommonSystem.COMPUTER_STORAGE_MODULE_TYPE + "\");\n" +
                        "var inventoryMod = computer.bindModuleOfType(\"" + InventoryModuleCommonSystem.COMPUTER_INVENTORY_MODULE_TYPE + "\");\n" +
                        "var inputInternal = storageMod.getInputInventoryBinding();\n" +
                        "var outputInternal = storageMod.getOutputInventoryBinding();\n" +
                        "var inputUp = inventoryMod.getInputInventoryBinding(\"up\");\n" +
                        "var outputUp = inventoryMod.getOutputInventoryBinding(\"up\");\n" +
                        "\n" +
                        "inventoryMod.dump(outputUp, inputInternal);\n" +
                        "os.waitFor(os.createSleepMs(5000));\n" +
                        "inventoryMod.dump(outputInternal, inputUp);");
    }

    @Override
    public int getCpuCycleDuration() {
        return 10;
    }

    @Override
    public Object onFunctionEnd(int line, ComputerCallback computer, Map<String, Variable> parameters, Object onFunctionStartResult) throws ExecutionException {
        return new InternalInventoryBindingCustomObject(input);
    }
}
