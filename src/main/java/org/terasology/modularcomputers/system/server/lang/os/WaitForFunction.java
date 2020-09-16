// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.modularcomputers.system.server.lang.os;

import org.terasology.modularcomputers.FunctionParamValidationUtil;
import org.terasology.modularcomputers.context.ComputerCallback;
import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.CustomObject;
import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.ExecutionException;
import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.Variable;
import org.terasology.modularcomputers.system.server.lang.TerasologyFunctionExecutable;
import org.terasology.modularcomputers.system.server.lang.os.condition.AbstractConditionCustomObject;

import java.util.Map;

public class WaitForFunction extends TerasologyFunctionExecutable {
    public WaitForFunction() {
        super("Waits for the specified condition to become true.", "any", "The result returned by the condition upon " +
                "meeting its requirement.");

        addParameter("condition", "Condition", "Condition to wait to become true.");

        addExample("This example creates a condition that waits for the specified number of milliseconds, waits " +
                        "for it and then prints out text to console.",
                "var sleepCondition = os.createSleepMs(3000);\n" +
                        "os.waitFor(sleepCondition);\n" +
                        "console.append(\"This text is printed after 3 seconds have passed.\");"
        );
    }

    @Override
    protected int getDuration() {
        return 10;
    }

    @Override
    protected Object executeFunction(int line, ComputerCallback computer, Map<String, Variable> parameters) throws ExecutionException {
        Variable conditionVar = FunctionParamValidationUtil.validateParameter(line, parameters, "condition", "waitFor"
                , Variable.Type.CUSTOM_OBJECT);

        if (!((CustomObject) conditionVar.getValue()).getType().contains("CONDITION")) {
            throw new ExecutionException(line, "Expected CONDITION in waitFor()");
        }

        final AbstractConditionCustomObject condition = (AbstractConditionCustomObject) conditionVar.getValue();
        computer.suspendWithCondition(condition.createAwaitingCondition());

        return null;
    }
}
