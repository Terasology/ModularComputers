// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.modularcomputers.system.server.lang.os;


import org.terasology.modularcomputers.context.ComputerCallback;
import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.ExecutionException;
import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.Variable;
import org.terasology.modularcomputers.system.server.lang.TerasologyFunctionExecutable;
import org.terasology.modularcomputers.system.server.lang.os.condition.AbstractConditionCustomObject;
import org.terasology.modularcomputers.system.server.lang.os.condition.ResultAwaitingCondition;

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

        @Override
        public void dispose() {

        }
    }
}
