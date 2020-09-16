// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.modularcomputers.system.server.lang.os;


import org.terasology.modularcomputers.context.ComputerCallback;
import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.CustomObject;
import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.ExecutionException;
import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.Variable;
import org.terasology.modularcomputers.system.server.lang.TerasologyFunctionExecutable;
import org.terasology.modularcomputers.system.server.lang.os.condition.AbstractConditionCustomObject;
import org.terasology.modularcomputers.system.server.lang.os.condition.AnyResultAwaitingCondition;
import org.terasology.modularcomputers.system.server.lang.os.condition.ResultAwaitingCondition;
import org.terasology.modularcomputers.ui.documentation.DocumentationBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AnyFunction extends TerasologyFunctionExecutable {
    public AnyFunction() {
        super("Creates a condition that becomes true, when any the conditions passed becomes true.", "Condition",
                "Condition that becomes true, when any of the passed conditions becomes true.<l>" +
                        "In addition when this condition is <h navigate:" + DocumentationBuilder.getBuiltInObjectMethodPageId("os", "waitFor") +
                        ">waitedFor</h> the waitFor for this " +
                        "condition will return an array containing two objects - index of the condition that became " +
                        "true, " +
                        "and the value returned by the condition.  If multiple conditions become true in the same " +
                        "tick, the condition with the lowest index in the list is used.");

        addParameter("conditions", "Array of Condition", "Conditions that this condition will wait for to become true" +
                ".");

        addExample("This example creates two conditions, one waiting for 3 seconds, the other for 5 seconds, waits " +
                        "for ANY of them and prints out text to console.",
                "var sleepCondition1 = os.createSleepMs(3000);\n" +
                        "var sleepCondition2 = os.createSleepMs(5000);\n" +
                        "var sleepAny = os.any([sleepCondition1, sleepCondition2]);\n" +
                        "os.waitFor(sleepAny);\n" +
                        "console.append(\"This text is printed after 3 seconds have passed.\");"
        );
    }

    @Override
    protected int getDuration() {
        return 10;
    }

    @Override
    protected Object executeFunction(int line, ComputerCallback computer, Map<String, Variable> parameters) throws ExecutionException {
        final Variable conditionsVar = parameters.get("conditions");
        if (conditionsVar.getType() != Variable.Type.LIST) {
            throw new ExecutionException(line, "Expected a LIST of CONDITIONs in any()");
        }

        List<Variable> conditions = (List<Variable>) conditionsVar.getValue();

        final List<AbstractConditionCustomObject> anyConditions = new ArrayList<AbstractConditionCustomObject>();
        for (Variable condition : conditions) {
            if (condition.getType() != Variable.Type.CUSTOM_OBJECT || !((CustomObject) condition.getValue()).getType().contains("CONDITION")) {
                throw new ExecutionException(line, "Expected a LIST of CONDITIONs in any()");
            }
            final AbstractConditionCustomObject conditionDefinition =
                    (AbstractConditionCustomObject) condition.getValue();
            anyConditions.add(conditionDefinition);
        }

        return new AbstractConditionCustomObject() {
            @Override
            public int sizeOf() {
                int size = 4;
                for (AbstractConditionCustomObject condition : anyConditions) {
                    size += condition.sizeOf();
                }

                return size;
            }


            @Override
            public ResultAwaitingCondition createAwaitingCondition() throws ExecutionException {
                List<ResultAwaitingCondition> anyConditionList = new ArrayList<ResultAwaitingCondition>();
                for (AbstractConditionCustomObject anyCondition : anyConditions) {
                    anyConditionList.add(anyCondition.createAwaitingCondition());
                }

                return new AnyResultAwaitingCondition(anyConditionList);
            }
        };
    }
}
