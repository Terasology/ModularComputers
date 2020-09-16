// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.modularcomputers.module.wireless;

import org.terasology.engine.core.Time;
import org.terasology.modularcomputers.FunctionParamValidationUtil;
import org.terasology.modularcomputers.context.ComputerCallback;
import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.ExecutionException;
import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.Variable;
import org.terasology.modularcomputers.system.server.lang.AbstractModuleMethodExecutable;

import java.util.Map;

public class SendMessageMethod extends AbstractModuleMethodExecutable<Object> {
    private final String methodName;
    private final Time time;
    private final int maxMessageLength;
    private final int maxMessageExpiry;

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
    public Object onFunctionEnd(int line, ComputerCallback computer, Map<String, Variable> parameters,
                                Object onFunctionStartResult) throws ExecutionException {
        CommunicationChannel communicationChannel =
                FunctionParamValidationUtil.validateCommunicationChannelBinding(line, parameters, 
                        "communicationChannel", methodName);
        String message = FunctionParamValidationUtil.validateStringParameter(line, parameters, "message", methodName);
        if (message.length() > maxMessageLength) {
            throw new ExecutionException(line, "Message exceeds maximum message length of " + maxMessageLength);
        }

        communicationChannel.sendMessage(message, computer, time.getGameTimeInMs() + maxMessageExpiry);
        return null;
    }
}
