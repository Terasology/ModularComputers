// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.modularcomputers.module.wireless;

import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.modularcomputers.FunctionParamValidationUtil;
import org.terasology.modularcomputers.context.ComputerCallback;
import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.ExecutionException;
import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.Variable;
import org.terasology.modularcomputers.system.server.lang.AbstractModuleMethodExecutable;

import java.util.Map;

public class CreatePrivateChannelBindingMethod extends AbstractModuleMethodExecutable<Object> {
    private final String methodName;
    private final CommunicationChannels<EntityRef> communicationChannels;
    private final float range;

    public CreatePrivateChannelBindingMethod(String methodName,
                                             CommunicationChannels<EntityRef> communicationChannels, float range) {
        super("Creates a private (to programs run by current player) channel binding to send to and receive messages " +
                "from.", "CommunicationChannelBinding", "Communication channel binding requested.");
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
    public Object onFunctionEnd(int line, ComputerCallback computer, Map<String, Variable> parameters,
                                Object onFunctionStartResult) throws ExecutionException {
        String channelName = FunctionParamValidationUtil.validateStringParameter(line, parameters, "channelName",
                methodName);
        return new PrivateCommunicationChannelCustomObject(channelName, range, communicationChannels);
    }
}
