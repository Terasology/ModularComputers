// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.modularcomputers.module.storage;

import org.terasology.modularcomputers.context.ComputerCallback;
import org.terasology.modularcomputers.module.inventory.InventoryModuleCommonSystem;
import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.ExecutionException;
import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.Variable;
import org.terasology.modularcomputers.system.server.lang.AbstractModuleMethodExecutable;

import java.util.Map;

public class StorageInventoryBindingMethod extends AbstractModuleMethodExecutable<Object> {
    private final boolean input;

    public StorageInventoryBindingMethod(boolean input) {
        super(input ? "Creates the input inventory binding for the Internal storage." : "Creates the output inventory" +
                        " binding for the Internal storage.",
                "InventoryBinding",
                input ? "Returns inventory binding allowing to put items into this Internal storage." : "Returns " +
                        "inventory binding allowing to extract items from this Internal storage.");
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
    public Object onFunctionEnd(int line, ComputerCallback computer, Map<String, Variable> parameters,
                                Object onFunctionStartResult) throws ExecutionException {
        return new InternalInventoryBindingCustomObject(input);
    }
}
