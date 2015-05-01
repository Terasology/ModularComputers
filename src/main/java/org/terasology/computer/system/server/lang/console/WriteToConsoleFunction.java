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
package org.terasology.computer.system.server.lang.console;

import com.gempukku.lang.ExecutionException;
import com.gempukku.lang.Variable;
import org.terasology.computer.FunctionParamValidationUtil;
import org.terasology.computer.context.ComputerCallback;
import org.terasology.computer.system.server.lang.TerasologyFunctionExecutable;

import java.util.Arrays;
import java.util.Map;

public class WriteToConsoleFunction extends TerasologyFunctionExecutable {
    @Override
    protected int getDuration() {
        return 100;
    }

    @Override
    public java.util.Collection<String> getParameterNames() {
        return Arrays.asList("x", "y", "text");
    }

    @Override
    protected Object executeFunction(int line, ComputerCallback computer, Map<String, Variable> parameters) throws ExecutionException {
        int x = FunctionParamValidationUtil.validateIntParameter(line, parameters, "x", "write");
        int y = FunctionParamValidationUtil.validateIntParameter(line, parameters, "y", "write");
        String text = FunctionParamValidationUtil.validateStringParameter(line, parameters, "text", "write");

        computer.getConsole().setCharacters(x, y, text);

        return null;
    }
}
