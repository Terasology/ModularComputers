// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
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
    public Object onFunctionEnd(int line, ComputerCallback computer, Map<String, Variable> parameters, Object onFunctionStartResult)
            throws ExecutionException {
        CommunicationChannel communicationChannel = FunctionParamValidationUtil.validateCommunicationChannelBinding(
                line, parameters, "communicationChannel", methodName);
        String message = FunctionParamValidationUtil.validateStringParameter(line, parameters, "message", methodName);
        if (message.length() > maxMessageLength) {
            throw new ExecutionException(line, "Message exceeds maximum message length of " + maxMessageLength);
        }

        communicationChannel.sendMessage(message, computer, time.getGameTimeInMs() + maxMessageExpiry);
        return null;
    }
}
