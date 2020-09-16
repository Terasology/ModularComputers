// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.modularcomputers.system.server.lang.console;

import org.terasology.modularcomputers.FunctionParamValidationUtil;
import org.terasology.modularcomputers.context.ComputerCallback;
import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.ExecutionException;
import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.Variable;
import org.terasology.modularcomputers.system.server.lang.TerasologyFunctionExecutable;

import java.util.Map;

public class WriteToConsoleFunction extends TerasologyFunctionExecutable {
    public WriteToConsoleFunction() {
        super("This method writes the specified text at the specified place in Computer console");

        addParameter("x", "Number", "X position of the text start in the console (column).");
        addParameter("y", "Number", "Y position of the text start in the console (row).");
        addParameter("text", "String", "Text to display at the specified position in the console.");

        addExample("This example program writes the specified text at the specified position in console.",
                "console.write(0, 5, \"This text is written on 6th line.\");"
        );
    }

    @Override
    protected int getDuration() {
        return 100;
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
