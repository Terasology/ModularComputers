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

public class CreateSleepMsFunction extends TerasologyFunctionExecutable {
    public CreateSleepMsFunction() {
        super("Creates a condition that waits for the specified time in milliseconds.", "Condition",
                "Condition that becomes true after specified number of milliseconds.");

        addParameter("time", "Number", "Number of milliseconds this condition should wait to become true.");

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
        final Variable timeVar = parameters.get("time");
        if (timeVar.getType() != Variable.Type.NUMBER) {
            throw new ExecutionException(line, "Expected NUMBER in createSleepMs()");
        }

        final long time = ((Number) timeVar.getValue()).longValue();
        if (time <= 0) {
            throw new ExecutionException(line, "Sleep time must be greater than 0");
        }

        return new AbstractConditionCustomObject() {
            @Override
            public int sizeOf() {
                return 4;
            }

            @Override
            public ResultAwaitingCondition createAwaitingCondition() {
                return new SystemTimeAwaitingCondition(System.currentTimeMillis() + time);
            }
        };
    }

    private static class SystemTimeAwaitingCondition implements ResultAwaitingCondition {
        private final long _finishAt;

        private SystemTimeAwaitingCondition(long finishAt) {
            _finishAt = finishAt;
        }

        @Override
        public boolean isMet() throws ExecutionException {
            return System.currentTimeMillis() >= _finishAt;
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
