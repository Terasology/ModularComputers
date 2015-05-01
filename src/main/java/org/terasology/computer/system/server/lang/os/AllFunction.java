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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class AllFunction extends TerasologyFunctionExecutable {
    @Override
    protected int getDuration() {
        return 10;
    }

    @Override
    public java.util.Collection<String> getParameterNames() {
        return Arrays.asList("conditions");
    }

    @Override
    protected Object executeFunction(int line, ComputerCallback computer, Map<String, Variable> parameters) throws ExecutionException {
        final Variable conditionsVar = parameters.get("conditions");
        if (conditionsVar.getType() != Variable.Type.LIST) {
            throw new ExecutionException(line, "Expected a LIST of CONDITIONs in all()");
        }

        List<Variable> conditions = (List<Variable>) conditionsVar.getValue();

        int delay = 0;
        final List<AbstractConditionCustomObject> allConditions = new ArrayList<>();
        for (Variable condition : conditions) {
            if (condition.getType() != Variable.Type.CUSTOM_OBJECT || !((CustomObject) condition.getValue()).getType().contains("CONDITION")) {
                throw new ExecutionException(line, "Expected a LIST of CONDITIONs in all()");
            }
            AbstractConditionCustomObject conditionDefinition = (AbstractConditionCustomObject) condition.getValue();
            delay = Math.max(delay, conditionDefinition.getCreationDelay());
            allConditions.add(conditionDefinition);
        }

        final int maxDelay = delay;

        return new AbstractConditionCustomObject() {
            @Override
            public int getCreationDelay() {
                return maxDelay;
            }

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
