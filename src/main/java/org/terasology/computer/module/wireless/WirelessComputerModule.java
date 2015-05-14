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

import org.terasology.computer.module.DefaultComputerModule;
import org.terasology.engine.Time;
import org.terasology.entitySystem.entity.EntityRef;

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
