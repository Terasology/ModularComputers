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
import java.util.Map;

public class FormatFunction extends TerasologyFunctionExecutable {
    public FormatFunction() {
        super("Formats the specified number using the format passed as a parameter. This behaves exactly like DecimalFormat " +
                "in Java language.", "String", "Formatted number as specified by parameters.");

        addParameter("format", "String", "Format to use to output the number, as specified in DecimalFormat class in Java language");
        addParameter("number", "Number", "Number to format.");

        addExample("This example prints out a floating-point number in a specified format.",
                "var oneThird = 1/3;\n" +
                        "console.append(os.format(\"0.00\", oneThird));"
        );
    }

    @Override
    protected int getDuration() {
        return 10;
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
