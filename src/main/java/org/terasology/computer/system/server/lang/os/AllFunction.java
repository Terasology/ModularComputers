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

import com.gempukku.lang.CustomObject;
import com.gempukku.lang.ExecutionException;
import com.gempukku.lang.Variable;
import org.terasology.computer.context.ComputerCallback;
import org.terasology.computer.system.server.lang.TerasologyFunctionExecutable;
import org.terasology.computer.system.server.lang.os.condition.AbstractConditionCustomObject;
import org.terasology.computer.system.server.lang.os.condition.AllResultAwaitingCondition;
import org.terasology.computer.system.server.lang.os.condition.ResultAwaitingCondition;
import org.terasology.computer.ui.documentation.DocumentationBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AllFunction extends TerasologyFunctionExecutable {
    public AllFunction() {
        super("Creates a condition that becomes true, when all the conditions passed become true.", "Condition", "Condition that becomes true, when all of the passed conditions become true.\n" +
                "In addition when this condition is <h navigate:" + DocumentationBuilder.getBuiltInObjectMethodPageId("os", "waitFor") +
                ">waitedFor</h> the waitFor for this " +
                "condition will return an array containing all the objects returned by the conditions, in the " +
                "order they appear in the original array passed as a parameter.");

        addParameter("conditions", "Array of Condition", "Conditions that this condition will wait for to become true.");

        addExample("This example creates two conditions, one waiting for 3 seconds, the other for 5 seconds, waits " +
                        "for ALL of them and prints out text to console.",
                "var sleepCondition1 = os.createSleepMs(3000);\n" +
                        "var sleepCondition2 = os.createSleepMs(5000);\n" +
                        "var sleepAny = os.all([sleepCondition1, sleepCondition2]);\n" +
                        "os.waitFor(sleepAny);\n" +
                        "console.append(\"This text is printed after 5 seconds have passed.\");"
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
            throw new ExecutionException(line, "Expected a LIST of CONDITIONs in all()");
        }

        List<Variable> conditions = (List<Variable>) conditionsVar.getValue();

        final List<AbstractConditionCustomObject> allConditions = new ArrayList<>();
        for (Variable condition : conditions) {
            if (condition.getType() != Variable.Type.CUSTOM_OBJECT || !((CustomObject) condition.getValue()).getType().contains("CONDITION")) {
                throw new ExecutionException(line, "Expected a LIST of CONDITIONs in all()");
            }
            AbstractConditionCustomObject conditionDefinition = (AbstractConditionCustomObject) condition.getValue();
            allConditions.add(conditionDefinition);
        }

        return new AbstractConditionCustomObject() {
            @Override
            public int sizeOf() {
                int size = 4;
                for (AbstractConditionCustomObject condition : allConditions) {
                    size += condition.sizeOf();
                }

                return size;
            }

            @Override
            public ResultAwaitingCondition createAwaitingCondition() {
                List<ResultAwaitingCondition> allConditionList = new ArrayList<>();
                for (AbstractConditionCustomObject allCondition : allConditions) {
                    allConditionList.add(allCondition.createAwaitingCondition());
                }

                return new AllResultAwaitingCondition(allConditionList);
            }
        };
    }
}
