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
import org.terasology.computer.system.server.lang.ModuleMethodExecutable;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.logic.inventory.InventoryUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InventoryAndChangeConditionMethod implements ModuleMethodExecutable<Object> {
    private final String methodName;
    private InventoryModuleConditionsRegister inventoryModuleConditionsRegister;

    public InventoryAndChangeConditionMethod(String methodName, InventoryModuleConditionsRegister inventoryModuleConditionsRegister) {
        this.inventoryModuleConditionsRegister = inventoryModuleConditionsRegister;
        this.methodName = methodName;
    }

    @Override
    public int getCpuCycleDuration() {
        return 200;
    }

    @Override
    public String[] getParameterNames() {
        return new String[] {"inventoryBinding"};
    }

    @Override
    public Object onFunctionEnd(int line, ComputerCallback computer, Map<String, Variable> parameters, Object onFunctionStartResult) throws ExecutionException {
        InventoryBinding.InventoryWithSlots inventory = FunctionParamValidationUtil.validateInventoryBinding(line, computer,
                parameters, "inventoryBinding", methodName, null);

        Map<String, Variable> result = new HashMap<>();

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

        result.put("inventory", new Variable(inventoryResult));

        result.put("condition", new Variable(inventoryModuleConditionsRegister.registerInventoryChangeListener(inventory.inventory)));

        return result;
    }
}
