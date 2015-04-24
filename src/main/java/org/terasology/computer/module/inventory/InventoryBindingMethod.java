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
import org.terasology.math.Direction;
import org.terasology.world.BlockEntityRegistry;

import java.util.Map;

public class InventoryBindingMethod implements ModuleMethodExecutable<Object> {
    private BlockEntityRegistry blockEntityRegistry;
    private boolean input;

    public InventoryBindingMethod(BlockEntityRegistry blockEntityRegistry, boolean input) {
        this.blockEntityRegistry = blockEntityRegistry;
        this.input = input;
    }

    @Override
    public int getCpuCycleDuration() {
        return 10;
    }

    @Override
    public String[] getParameterNames() {
        return new String[] { "direction" };
    }

    @Override
    public Object onFunctionEnd(int line, ComputerCallback computer, Map<String, Variable> parameters, Object onFunctionStartResult) throws ExecutionException {
        Direction direction = FunctionParamValidationUtil.validateDirectionParameter(line, parameters,
                "direction", "getInventoryBinding");

        return new RelativeInventoryBindingCustomObject(blockEntityRegistry, direction, input);
    }
}
