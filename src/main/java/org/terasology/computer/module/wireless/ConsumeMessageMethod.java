// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.computer.module.wireless;

import com.gempukku.lang.ExecutionException;
import com.gempukku.lang.Variable;
import org.terasology.computer.FunctionParamValidationUtil;
import org.terasology.computer.context.ComputerCallback;
import org.terasology.computer.system.server.lang.AbstractModuleMethodExecutable;
import org.terasology.engine.core.Time;

import java.util.Map;

public class ConsumeMessageMethod extends AbstractModuleMethodExecutable<Object> {
    private final String methodName;
    private final Time time;

    public ConsumeMessageMethod(String methodName, Time time) {
        super("Consumes next message from the specified communication channel.", "Map", "Returns a map with two keys " +
                "- " +
                "\"message\" that has a value type of String and contains message sent and " +
                "\"location\" that has a value type of String in a format of x,y,z and contains location of the " +
                "height of the binding.<l>" +
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
    public Object onFunctionEnd(int line, ComputerCallback computer, Map<String, Variable> parameters,
                                Object onFunctionStartResult) throws ExecutionException {
        CommunicationChannel communicationChannel =
                FunctionParamValidationUtil.validateCommunicationChannelBinding(line, parameters, 
                        "communicationChannel", methodName);

        return communicationChannel.consumeMessage(time.getGameTimeInMs(), computer);
    }
}
