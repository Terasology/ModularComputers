// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.modularcomputers.module.wireless;

import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.math.geom.Vector3i;
import org.terasology.modularcomputers.context.ComputerCallback;
import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.CustomObject;
import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.ExecutionException;
import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.Variable;
import org.terasology.modularcomputers.system.server.lang.os.condition.AbstractConditionCustomObject;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class SecureCommunicationChannelCustomObject implements CustomObject, CommunicationChannel {
    private final String channelName;
    private final String password;
    private final float range;
    private final CommunicationChannels<EntityRef> communicationChannels;

    public SecureCommunicationChannelCustomObject(String channelName, String password, float range,
                                                  CommunicationChannels<EntityRef> communicationChannels) {
        this.channelName = channelName;
        this.password = password;
        this.range = range;
        this.communicationChannels = communicationChannels;
    }

    @Override
    public void sendMessage(String message, ComputerCallback computer, long expireOn) {
        communicationChannels.addSecureMessage(channelName, password, new Vector3i(computer.getComputerLocation()),
                range, message, expireOn);
    }

    @Override
    public Map<String, Variable> consumeMessage(long currentTime, ComputerCallback computer) {
        return communicationChannels.consumeNextSecureMessage(currentTime, channelName, password,
                new Vector3i(computer.getComputerLocation()), range);
    }

    @Override
    public AbstractConditionCustomObject consumeMessageCondition(long currentTime, ComputerCallback computer) {
        return new SecureMessageAwaitingLatchCondition(new Vector3i(computer.getComputerLocation()), range, password) {
            @Override
            protected Runnable registerAwaitingCondition() throws ExecutionException {
                Map<String, Variable> message = consumeMessage(currentTime, computer);
                if (message != null) {
                    release(message);
                    return null;
                } else {
                    communicationChannels.addSecureMessageCondition(channelName, this);
                    SecureMessageAwaitingLatchCondition thisCondition = this;
                    return new Runnable() {
                        @Override
                        public void run() {
                            communicationChannels.removeSecureMessageCondition(channelName, thisCondition);
                        }
                    };
                }
            }
        };
    }

    @Override
    public Collection<String> getType() {
        return Collections.singleton("COMMUNICATION_CHANNEL");
    }

    @Override
    public int sizeOf() {
        return 4;
    }
}
