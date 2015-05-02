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
import org.terasology.computer.FunctionParamValidationUtil;
import org.terasology.computer.context.ComputerCallback;
import org.terasology.computer.system.server.lang.TerasologyFunctionExecutable;
import org.terasology.computer.system.server.lang.os.condition.AbstractConditionCustomObject;

import java.util.Map;

public class WaitForFunction extends TerasologyFunctionExecutable {
    public WaitForFunction() {
        super("Waits for the specified condition to become true.", "any", "The result returned by the condition upon meeting its requirement.");

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
        Variable conditionVar = FunctionParamValidationUtil.validateParameter(line, parameters, "condition", "waitFor", Variable.Type.CUSTOM_OBJECT);

        if (!((CustomObject) conditionVar.getValue()).getType().contains("CONDITION")) {
            throw new ExecutionException(line, "Expected CONDITION in waitFor()");
        }

        final AbstractConditionCustomObject condition = (AbstractConditionCustomObject) conditionVar.getValue();
        computer.suspendWithCondition(condition.createAwaitingCondition());

        return null;
    }
}
