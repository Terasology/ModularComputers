// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.computer.module.wireless;

import com.gempukku.lang.CustomObject;
import com.gempukku.lang.ExecutionException;
import com.gempukku.lang.Variable;
import org.joml.RoundingMode;
import org.joml.Vector3i;
import org.terasology.computer.context.ComputerCallback;
import org.terasology.computer.system.server.lang.os.condition.AbstractConditionCustomObject;
import org.terasology.engine.entitySystem.entity.EntityRef;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class SecureCommunicationChannelCustomObject implements CustomObject, CommunicationChannel {
    private String channelName;
    private String password;
    private float range;
    private CommunicationChannels<EntityRef> communicationChannels;

    public SecureCommunicationChannelCustomObject(String channelName, String password, float range,
                                                  CommunicationChannels<EntityRef> communicationChannels) {
        this.channelName = channelName;
        this.password = password;
        this.range = range;
        this.communicationChannels = communicationChannels;
    }

    @Override
    public void sendMessage(String message, ComputerCallback computer, long expireOn) {
        communicationChannels.addSecureMessage(channelName, password,
                new Vector3i(computer.getComputerLocation(), RoundingMode.FLOOR), range, message, expireOn);
    }

    @Override
    public Map<String, Variable> consumeMessage(long currentTime, ComputerCallback computer) {
        return communicationChannels.consumeNextSecureMessage(currentTime, channelName, password,
                new Vector3i(computer.getComputerLocation(), RoundingMode.FLOOR), range);
    }

    @Override
    public AbstractConditionCustomObject consumeMessageCondition(long currentTime, ComputerCallback computer) {
        return new SecureMessageAwaitingLatchCondition(new Vector3i(computer.getComputerLocation(), RoundingMode.FLOOR), range, password) {
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
            }};
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
