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
import org.terasology.engine.core.Time;

import java.util.Map;

public class SendMessageMethod extends AbstractModuleMethodExecutable<Object> {
    private String methodName;
    private Time time;
    private int maxMessageLength;
    private int maxMessageExpiry;

    public SendMessageMethod(String methodName, Time time, int maxMessageLength, int maxMessageExpiry) {
        super("Sends message to the specified communication channel.");
        this.methodName = methodName;
        this.time = time;
        this.maxMessageLength = maxMessageLength;
        this.maxMessageExpiry = maxMessageExpiry;

        addParameter("communicationChannel", "CommunicationChannelBinding", "Channel to send to");
        addParameter("message", "String", "Message to send");
    }

    @Override
    public int getCpuCycleDuration() {
        return 100;
    }

    @Override
    public int getMinimumExecutionTime(int line, ComputerCallback computer, Map<String, Variable> parameters) throws ExecutionException {
        return 100;
    }

    @Override
    public Object onFunctionEnd(int line, ComputerCallback computer, Map<String, Variable> parameters, Object onFunctionStartResult) throws ExecutionException {
        CommunicationChannel communicationChannel = FunctionParamValidationUtil.validateCommunicationChannelBinding(line, parameters, "communicationChannel", methodName);
        String message = FunctionParamValidationUtil.validateStringParameter(line, parameters, "message", methodName);
        if (message.length() > maxMessageLength) {
            throw new ExecutionException(line, "Message exceeds maximum message length of " + maxMessageLength);
        }

        communicationChannel.sendMessage(message, computer, time.getGameTimeInMs()+maxMessageExpiry);
        return null;
    }
}
