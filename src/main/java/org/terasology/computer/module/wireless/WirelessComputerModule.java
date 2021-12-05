// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.computer.module.wireless;

import org.terasology.computer.module.DefaultComputerModule;
import org.terasology.engine.core.Time;
import org.terasology.engine.entitySystem.entity.EntityRef;

public class WirelessComputerModule extends DefaultComputerModule {
    public WirelessComputerModule(CommunicationChannels<EntityRef> communicationChannels, Time time,
                                  int maxMessageLength, int maxMessageExpiry,
                                  float range, String moduleType, String moduleName) {
        super(moduleType, moduleName);

        addMethod("bindPublicChannel",
                new CreatePublicChannelBindingMethod("bindPublicChannel", communicationChannels, range));
        addMethod("bindPrivateChannel",
                new CreatePrivateChannelBindingMethod("bindPrivateChannel", communicationChannels, range));
        addMethod("bindSecretChannel",
                new CreateSecureChannelBindingMethod("bindSecretChannel", communicationChannels, range));

        addMethod("sendMessage",
                new SendMessageMethod("sendMessage", time, maxMessageLength, maxMessageExpiry));
        addMethod("consumeMessage",
                new ConsumeMessageMethod("consumeMessage", time));
        addMethod("consumeMessageCondition",
                new ConsumeMessageConditionMethod("consumeMessageCondition", time));
    }
}
