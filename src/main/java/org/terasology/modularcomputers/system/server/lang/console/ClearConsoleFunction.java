// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.modularcomputers.system.server.lang.console;

import org.terasology.modularcomputers.context.ComputerCallback;
import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.ExecutionException;
import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.Variable;
import org.terasology.modularcomputers.system.server.lang.TerasologyFunctionExecutable;

import java.util.Map;


public class ClearConsoleFunction extends TerasologyFunctionExecutable {
    public ClearConsoleFunction() {
        super("This method clears everything int the Computer console.");

        addExample("This example program clears the screen of the Computer Console. Please note, it first appends " +
                        "a line of text to it, to show it actually works.",
                "console.append(\"Line of text you won't see.\");\n" +
                        "console.clear();"
        );
    }

    @Override
    protected int getDuration() {
        return 100;
    }

    @Override
    protected Object executeFunction(int line, ComputerCallback computer, Map<String, Variable> parameters) throws ExecutionException {
        computer.getConsole().clearConsole();
        return null;
    }
}
