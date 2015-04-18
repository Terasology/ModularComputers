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

import com.gempukku.lang.*;
import org.terasology.computer.context.ComputerCallback;
import org.terasology.computer.context.TerasologyComputerExecutionContext;
import org.terasology.computer.system.server.lang.os.condition.AbstractConditionCustomObject;
import org.terasology.computer.system.server.lang.os.condition.ResultAwaitingCondition;

public class WaitForFunction implements FunctionExecutable {
    @Override
    public CallContext getCallContext() {
        return new CallContext(null, false, false);
    }

    @Override
    public String[] getParameterNames() {
        return new String[]{"condition"};
    }

    @Override
    public Execution createExecution(final int line, ExecutionContext executionContext, CallContext callContext) {
        return new Execution() {
            private boolean _suspended;
            private boolean _retrievedResult;
            private ResultAwaitingCondition _condition;

            @Override
            public boolean hasNextExecution(ExecutionContext executionContext) {
                return !_retrievedResult;
            }

            @Override
            public ExecutionProgress executeNextStatement(ExecutionContext executionContext, ExecutionCostConfiguration configuration) throws ExecutionException {
                if (!_suspended) {
                    final Variable conditionVar = executionContext.peekCallContext().getVariableValue("condition");
                    if (conditionVar.getType() != Variable.Type.CUSTOM_OBJECT || !((CustomObject) conditionVar.getValue()).getType().equals("CONDITION"))
                        throw new ExecutionException(line, "Expected CONDITION in waitFor()");

                    final AbstractConditionCustomObject condition = (AbstractConditionCustomObject) conditionVar.getValue();

                    final TerasologyComputerExecutionContext minecraftExecutionContext = (TerasologyComputerExecutionContext) executionContext;
                    final ComputerCallback computerData = minecraftExecutionContext.getComputerCallback();

                    _condition = condition.createAwaitingCondition();

                    computerData.suspendWithCondition(_condition);

                    _suspended = true;
                    return new ExecutionProgress(condition.getCreationDelay());
                }
                if (!_retrievedResult) {
                    executionContext.setContextValue(_condition.getReturnValue());
                    _retrievedResult = true;
                    return new ExecutionProgress(configuration.getSetContextValue());
                }
                return null;
            }
        };
    }
}
