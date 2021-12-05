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

public class ConsumeMessageConditionMethod extends AbstractModuleMethodExecutable<Object> {
    private String methodName;
    private Time time;

    public ConsumeMessageConditionMethod(String methodName, Time time) {
        super("Creates a condition that consumes next message when it becomes available. " +
                "It waits for the message to become available on the specified communication channel.",
                "Condition", "Returns a condition that when is satisfied, returns a map with two keys - " +
                "\"message\" that has a value type of String and contains message sent and " +
                "\"location\" that has a value type of String in a format of x,y,z and contains location of the height of the binding.");
        this.methodName = methodName;
        this.time = time;

        addParameter("communicationChannel", "CommunicationChannelBinding", "Channel to wait on");
    }

    @Override
    public int getCpuCycleDuration() {
        return 100;
    }

    @Override
    public Object onFunctionEnd(int line, ComputerCallback computer, Map<String, Variable> parameters, Object onFunctionStartResult)
            throws ExecutionException {
        CommunicationChannel communicationChannel = FunctionParamValidationUtil.validateCommunicationChannelBinding(
                line, parameters, "communicationChannel", methodName);

        return communicationChannel.consumeMessageCondition(time.getGameTimeInMs(), computer);
    }
}
