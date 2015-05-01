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
package org.terasology.computer.system.server.lang.computer;

import com.gempukku.lang.ExecutionException;
import com.gempukku.lang.Variable;
import org.terasology.computer.FunctionParamValidationUtil;
import org.terasology.computer.context.ComputerCallback;
import org.terasology.computer.system.server.lang.ComputerModule;
import org.terasology.computer.system.server.lang.TerasologyFunctionExecutable;
import org.terasology.computer.system.server.lang.computer.bind.SlotBindingObjectDefinition;

import java.util.Arrays;
import java.util.Map;

public class BindFirstModuleOfTypeFunction extends TerasologyFunctionExecutable {
    @Override
    protected int getDuration() {
        return 100;
    }

    @Override
    public java.util.Collection<String> getParameterNames() {
        return Arrays.asList("type");
    }

    @Override
    protected Object executeFunction(int line, ComputerCallback computer, Map<String, Variable> parameters) throws ExecutionException {
        String moduleType = FunctionParamValidationUtil.validateStringParameter(line, parameters, "type", "bindModuleOfType");

        final int moduleSlotsCount = computer.getModuleSlotsCount();
        for (int i = 0; i < moduleSlotsCount; i++) {
            final ComputerModule module = computer.getModule(i);
            if (module != null && module.getModuleType().equals(moduleType)) {
                return new SlotBindingObjectDefinition(i);
            }
        }
        throw new ExecutionException(line, "Couldn't find module of the specified type.");
    }
}
