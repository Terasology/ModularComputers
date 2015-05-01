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

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

public class CreateSleepMsFunction extends TerasologyFunctionExecutable {
    @Override
    protected int getDuration() {
        return 10;
    }

    @Override
    public Collection<String> getParameterNames() {
        return Arrays.asList("time");
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
            public int getCreationDelay() {
                return 0;
            }

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
        private long _finishAt;

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
    }
}
