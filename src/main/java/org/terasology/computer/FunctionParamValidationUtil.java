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
package org.terasology.computer;

import com.gempukku.lang.CustomObject;
import com.gempukku.lang.ExecutionException;
import com.gempukku.lang.Variable;
import org.terasology.computer.context.ComputerCallback;
import org.terasology.computer.module.ComputerDirection;
import org.terasology.computer.module.inventory.InventoryBinding;
import org.terasology.computer.module.wireless.CommunicationChannel;
import org.terasology.math.Direction;

import java.util.Map;

public class FunctionParamValidationUtil {
    private FunctionParamValidationUtil() {
    }

    public static Variable validateParameter(int line, Map<String, Variable> parameters,
                                             String parameterName, String functionName, Variable.Type expectedType) throws ExecutionException {
        Variable var = parameters.get(parameterName);
        if (var.getType() != expectedType)
            throw new ExecutionException(line, "Invalid " + parameterName + " in " + functionName + "()");
        return var;
    }

    public static String validateStringParameter(int line, Map<String, Variable> parameters,
                                                 String parameterName, String functionName) throws ExecutionException {
        Variable var = validateParameter(line, parameters, parameterName, functionName, Variable.Type.STRING);
        return (String) var.getValue();
    }

    public static int validateIntParameter(int line, Map<String, Variable> parameters,
                                           String parameterName, String functionName) throws ExecutionException {
        Variable var = validateParameter(line, parameters, parameterName, functionName, Variable.Type.NUMBER);
        return ((Number) var.getValue()).intValue();
    }

    public static float validateFloatParameter(int line, Map<String, Variable> parameters,
                                               String parameterName, String functionName) throws ExecutionException {
        Variable var = validateParameter(line, parameters, parameterName, functionName, Variable.Type.NUMBER);
        return ((Number) var.getValue()).floatValue();
    }

    public static boolean validateBooleanParameter(int line, Map<String, Variable> parameters,
                                                   String parameterName, String functionName) throws ExecutionException {
        Variable var = validateParameter(line, parameters, parameterName, functionName, Variable.Type.BOOLEAN);
        return ((Boolean) var.getValue());
    }

    public static Direction validateDirectionParameter(
            int line, Map<String, Variable> parameters,
            String parameterName, String functionName) throws ExecutionException {
        Variable directionVar = FunctionParamValidationUtil.validateParameter(line, parameters, parameterName, functionName, Variable.Type.STRING);
        Direction direction = ComputerDirection.getDirection((String) directionVar.getValue());
        if (direction == null) {
            throw new ExecutionException(line, "Invalid " + direction + " in " + functionName + "()");
        }
        return direction;
    }

    public static InventoryBinding.InventoryWithSlots validateInventoryBinding(
            int line, ComputerCallback computer, Map<String, Variable> parameters,
            String parameterName, String functionName, Boolean input) throws ExecutionException {
        Variable inventoryBinding = validateParameter(line, parameters, parameterName, functionName, Variable.Type.CUSTOM_OBJECT);
        CustomObject customObject = (CustomObject) inventoryBinding.getValue();
        if (!customObject.getType().contains("INVENTORY_BINDING")
                || (input != null && input != ((InventoryBinding) customObject).isInput()))
            throw new ExecutionException(line, "Invalid " + parameterName + " in " + functionName + "()");

        InventoryBinding binding = (InventoryBinding) inventoryBinding.getValue();
        return binding.getInventoryEntity(line, computer);
    }

    public static int validateSlotNo(int line, Map<String, Variable> parameters, InventoryBinding.InventoryWithSlots inventory, String parameterName, String functionName) throws ExecutionException {
        Variable slot = validateParameter(line, parameters, parameterName, functionName, Variable.Type.NUMBER);

        int slotNo = ((Number) slot.getValue()).intValue();

        int slotCount = inventory.slots.size();

        if (slotNo < 0 || slotCount <= slotNo)
            throw new ExecutionException(line, "Slot number out of range in " + functionName + "()");
        return slotNo;
    }

    public static CommunicationChannel validateCommunicationChannelBinding(
            int line, Map<String, Variable> parameters, String parameterName, String methodName) throws ExecutionException {
        Variable channelBinding = validateParameter(line, parameters, parameterName, methodName, Variable.Type.CUSTOM_OBJECT);
        CustomObject customObject = (CustomObject) channelBinding.getValue();
        if (!customObject.getType().contains("COMMUNICATION_CHANNEL"))
            throw new ExecutionException(line, "Invalid " + parameterName + " in " + methodName + "()");

        return (CommunicationChannel) channelBinding.getValue();
    }
}
