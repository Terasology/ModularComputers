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

import java.util.Map;

public class ParseFloatFunction extends TerasologyFunctionExecutable {
    public ParseFloatFunction() {
        super("Parses the specified text as a float number.", "Number", "The number it was able to parse.");

        addParameter("text", "String", "Text to parse as a float number.");

        addExample("This example program parses a specified text into a variable of type Number. Please note " +
                        "the output has the \".0\" appended at the end as any whole number has, when printed " +
                        "on the screen.",
                "console.append(\"\"+os.parseFloat(\"123\"));"
        );
    }

    @Override
    protected int getDuration() {
        return 10;
    }

    @Override
    protected Object executeFunction(int line, ComputerCallback computer, Map<String, Variable> parameters) throws ExecutionException {
        final Variable text = parameters.get("text");
        if (text.getType() != Variable.Type.STRING)
            throw new ExecutionException(line, "Expected STRING in parseFloat()");
        try {
            return Float.parseFloat((String) text.getValue());
        } catch (NumberFormatException exp) {
            throw new ExecutionException(line, "Number format exception: " + text.getValue() + " in parseFloat()");
        }
    }
}
