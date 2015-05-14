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
package org.terasology.computer.module.wireless;

import com.gempukku.lang.ExecutionException;
import com.gempukku.lang.Variable;
import org.terasology.computer.FunctionParamValidationUtil;
import org.terasology.computer.context.ComputerCallback;
import org.terasology.computer.system.server.lang.AbstractModuleMethodExecutable;
import org.terasology.engine.Time;

import java.util.Map;

public class ConsumeMessageMethod extends AbstractModuleMethodExecutable<Object> {
    private String methodName;
    private Time time;

    public ConsumeMessageMethod(String methodName, Time time) {
        super("Consumes next message from the specified communication channel.", "Map", "Returns a map with two keys - " +
                "\"message\" that has a value type of String and contains message sent and " +
                "\"location\" that has a value type of String in a format of x,y,z and contains location of the height of the binding.<l>" +
                "If there is no message waiting in the channel - null will be returned.");
        this.methodName = methodName;
        this.time = time;

        addParameter("communicationChannel", "CommunicationChannelBinding", "Channel to consume from");
    }

    @Override
    public int getCpuCycleDuration() {
        return 50;
    }

    @Override
    public Object onFunctionEnd(int line, ComputerCallback computer, Map<String, Variable> parameters, Object onFunctionStartResult) throws ExecutionException {
        CommunicationChannel communicationChannel = FunctionParamValidationUtil.validateCommunicationChannelBinding(line, parameters, "communicationChannel", methodName);

        return communicationChannel.consumeMessage(time.getGameTimeInMs(), computer);
    }
}
