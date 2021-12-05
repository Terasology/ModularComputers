// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.computer.module.wireless;

import com.gempukku.lang.ExecutionException;
import com.gempukku.lang.Variable;
import org.terasology.computer.FunctionParamValidationUtil;
import org.terasology.computer.context.ComputerCallback;
import org.terasology.computer.system.server.lang.AbstractModuleMethodExecutable;
import org.terasology.engine.entitySystem.entity.EntityRef;

import java.util.Map;

public class CreatePublicChannelBindingMethod extends AbstractModuleMethodExecutable<Object> {
    private String methodName;
    private CommunicationChannels<EntityRef> communicationChannels;
    private float range;

    public CreatePublicChannelBindingMethod(String methodName, CommunicationChannels<EntityRef> communicationChannels, float range) {
        super("Creates a public channel binding to send to and receive messages from.",
                "CommunicationChannelBinding", "Communication channel binding requested.");
        this.methodName = methodName;
        this.communicationChannels = communicationChannels;
        this.range = range;

        addParameter("channelName", "String", "Channel name");
    }

    @Override
    public int getCpuCycleDuration() {
        return 50;
    }

    @Override
    public Object onFunctionEnd(int line, ComputerCallback computer, Map<String, Variable> parameters, Object onFunctionStartResult)
            throws ExecutionException {
        String channelName = FunctionParamValidationUtil.validateStringParameter(line, parameters, "channelName", methodName);
        return new PublicCommunicationChannelCustomObject(channelName, range, communicationChannels);
    }
}
