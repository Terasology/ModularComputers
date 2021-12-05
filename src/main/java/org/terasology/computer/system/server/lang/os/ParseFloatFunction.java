// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
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
        if (text.getType() != Variable.Type.STRING) {
            throw new ExecutionException(line, "Expected STRING in parseFloat()");
        }
        try {
            return Float.parseFloat((String) text.getValue());
        } catch (NumberFormatException exp) {
            throw new ExecutionException(line, "Number format exception: " + text.getValue() + " in parseFloat()");
        }
    }
}
