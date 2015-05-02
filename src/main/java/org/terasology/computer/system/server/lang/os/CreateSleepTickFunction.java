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
import org.terasology.computer.system.server.lang.os.condition.AbstractConditionCustomObject;
import org.terasology.computer.system.server.lang.os.condition.ResultAwaitingCondition;

import java.util.Map;

public class CreateSleepTickFunction extends TerasologyFunctionExecutable {
    public CreateSleepTickFunction() {
        super("Creates a condition that waits for the specified number of ticks.", "Condition",
                "Condition that becomes true after specified number of ticks.");

        addParameter("ticks", "Number", "Number of ticks this condition should wait to become true.");

        addExample("This example creates a condition that waits for the specified number of ticks, waits " +
                        "for it and then prints out text to console.",
                "var sleepCondition = os.createSleepTick(10);\n" +
                        "os.waitFor(sleepCondition);\n" +
                        "console.append(\"This text is printed after 10 ticks have passed.\");"
        );
    }

    @Override
    protected int getDuration() {
        return 10;
    }

    @Override
    protected Object executeFunction(int line, ComputerCallback computer, Map<String, Variable> parameters) throws ExecutionException {
        final Variable ticksVar = parameters.get("ticks");
        if (ticksVar.getType() != Variable.Type.NUMBER)
            throw new ExecutionException(line, "Expected NUMBER in createSleepTick()");

        final int ticks = ((Number) ticksVar.getValue()).intValue();
        if (ticks <= 0)
            throw new ExecutionException(line, "Sleep ticks must be greater than 0");

        return new AbstractConditionCustomObject() {
            @Override
            public int sizeOf() {
                return 4;
            }

            @Override
            public ResultAwaitingCondition createAwaitingCondition() {
                return new TicksAwaitingCondition(ticks);
            }
        };
    }

    private static class TicksAwaitingCondition implements ResultAwaitingCondition {
        private int _ticks;

        private TicksAwaitingCondition(int ticks) {
            _ticks = ticks;
        }

        @Override
        public boolean isMet() throws ExecutionException {
            _ticks--;
            return _ticks < 0;
        }

        @Override
        public Variable getReturnValue() {
            return new Variable(null);
        }
    }
}
