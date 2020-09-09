// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.computer.module.wireless;

import com.gempukku.lang.ExecutionException;
import com.gempukku.lang.Variable;
import org.terasology.computer.FunctionParamValidationUtil;
import org.terasology.computer.context.ComputerCallback;
import org.terasology.computer.system.server.lang.AbstractModuleMethodExecutable;
import org.terasology.engine.entitySystem.entity.EntityRef;

import java.util.Map;

public class CreateSecureChannelBindingMethod extends AbstractModuleMethodExecutable<Object> {
    private final String methodName;
    private final CommunicationChannels<EntityRef> communicationChannels;
    private final float range;

    public CreateSecureChannelBindingMethod(String methodName, CommunicationChannels<EntityRef> communicationChannels
            , float range) {
        super("Creates a secure (with specified password) channel binding to send to and receive messages from.",
                "CommunicationChannelBinding", "Communication channel binding requested.");
        this.methodName = methodName;
        this.communicationChannels = communicationChannels;
        this.range = range;

        addParameter("channelName", "String", "Channel name");
        addParameter("password", "String", "Password for the secure channel");
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
        String password = FunctionParamValidationUtil.validateStringParameter(line, parameters, "password", methodName);
        return new SecureCommunicationChannelCustomObject(channelName, password, range, communicationChannels);
    }
}
