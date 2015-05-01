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
package org.terasology.computer.system.server.lang.os;

import com.gempukku.lang.ExecutionException;
import com.gempukku.lang.Variable;
import org.terasology.computer.context.ComputerCallback;
import org.terasology.computer.system.server.lang.TerasologyFunctionExecutable;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

public class FormatFunction extends TerasologyFunctionExecutable {
    @Override
    protected int getDuration() {
        return 10;
    }

    @Override
    public Collection<String> getParameterNames() {
        return Arrays.asList("format", "number");
    }

    @Override
    protected Object executeFunction(int line, ComputerCallback computer, Map<String, Variable> parameters) throws ExecutionException {
        final Variable formatVar = parameters.get("format");
        if (formatVar.getType() != Variable.Type.STRING)
            throw new ExecutionException(line, "Expected STRING pattern in format()");
        String format = (String) formatVar.getValue();

        final Variable numberVar = parameters.get("number");
        if (numberVar.getType() != Variable.Type.NUMBER)
            throw new ExecutionException(line, "Expected NUMBER in format()");

        float number = ((Number) numberVar.getValue()).floatValue();

        try {
            return new DecimalFormat(format).format(number);
        } catch (IllegalArgumentException exp) {
            throw new ExecutionException(line, "Invalid format pattern " + format + " in format()");
        }
    }
}
