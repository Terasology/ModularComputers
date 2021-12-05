// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.computer.system.server.lang.os;

import com.gempukku.lang.CustomObject;
import com.gempukku.lang.ExecutionException;
import com.gempukku.lang.Variable;
import org.terasology.computer.context.ComputerCallback;
import org.terasology.computer.system.server.lang.TerasologyFunctionExecutable;

import java.util.Map;

public class TypeOfFunction extends TerasologyFunctionExecutable {
    public TypeOfFunction() {
        super("Returns the type of the variable passed to it.", "String", "Type of the variable.");

        addParameter("value", "any", "Value to return type for.");

        addExample("This example program prints the type of a couple of different variables.",
                "console.append(os.typeOf(\"text\"));\n" +
                        "console.append(os.typeOf(123));\n" +
                        "console.append(os.typeOf(true));\n" +
                        "console.append(os.typeOf(console));\n" +
                        "console.append(os.typeOf(null));"
        );
    }

    @Override
    protected int getDuration() {
        return 10;
    }

    @Override
    protected Object executeFunction(int line, ComputerCallback computer, Map<String, Variable> parameters) throws ExecutionException {
        final Variable value = parameters.get("value");
        final Variable.Type type = value.getType();
        if (type == Variable.Type.CUSTOM_OBJECT) {
            return ((CustomObject) value.getValue()).getType().toString();
        }

        return type.toString();
    }
}
