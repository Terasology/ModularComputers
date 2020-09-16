// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.modularcomputers.system.server.lang.console;


import org.terasology.modularcomputers.FunctionParamValidationUtil;
import org.terasology.modularcomputers.context.ComputerCallback;
import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.ExecutionException;
import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.Variable;
import org.terasology.modularcomputers.system.server.lang.TerasologyFunctionExecutable;

import java.util.Map;

public class AppendToConsoleFunction extends TerasologyFunctionExecutable {
    public AppendToConsoleFunction() {
        super("This method appends the specified text as a new line in Computer console.");

        addParameter("text", "String", "Text to display in the Computer console.");

        addExample(
                "This example program appends specified text to Computer Console.",
                "console.append(\"Hello World!\");"
        );
    }

    @Override
    protected int getDuration() {
        return 100;
    }

    @Override
    protected Object executeFunction(int line, ComputerCallback computer, Map<String, Variable> parameters) throws ExecutionException {
        String text = FunctionParamValidationUtil.validateStringParameter(line, parameters, "text", "append");
        computer.getConsole().appendString(text);
        return null;
    }
}
