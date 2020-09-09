// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.computer.module.wireless;

import com.gempukku.lang.CustomObject;
import com.gempukku.lang.ExecutionException;
import com.gempukku.lang.Variable;
import org.terasology.computer.context.ComputerCallback;
import org.terasology.computer.system.server.lang.os.condition.AbstractConditionCustomObject;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.math.geom.Vector3i;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class PrivateCommunicationChannelCustomObject implements CustomObject, CommunicationChannel {
    private final String channelName;
    private final float range;
    private final CommunicationChannels<EntityRef> communicationChannels;

    public PrivateCommunicationChannelCustomObject(String channelName, float range,
                                                   CommunicationChannels<EntityRef> communicationChannels) {
        this.channelName = channelName;
        this.range = range;
        this.communicationChannels = communicationChannels;
    }

    @Override
    public void sendMessage(String message, ComputerCallback computer, long expireOn) {
        communicationChannels.addPrivateMessage(channelName, computer.getExecutedBy(),
                new Vector3i(computer.getComputerLocation()), range, message, expireOn);
    }

    @Override
    public Map<String, Variable> consumeMessage(long currentTime, ComputerCallback computer) {
        return communicationChannels.consumeNextPrivateMessage(currentTime, channelName, computer.getExecutedBy(),
                new Vector3i(computer.getComputerLocation()), range);
    }

    @Override
    public AbstractConditionCustomObject consumeMessageCondition(long currentTime, ComputerCallback computer) {
        return new MessageAwaitingLatchCondition(new Vector3i(computer.getComputerLocation()), range) {
            @Override
            protected Runnable registerAwaitingCondition() throws ExecutionException {
                Map<String, Variable> message = consumeMessage(currentTime, computer);
                if (message != null) {
                    release(message);
                    return null;
                } else {
                    communicationChannels.addPrivateMessageCondition(channelName, computer.getExecutedBy(), this);
                    MessageAwaitingLatchCondition thisCondition = this;
                    return new Runnable() {
                        @Override
                        public void run() {
                            communicationChannels.removePrivateMessageCondition(channelName, computer.getExecutedBy()
                                    , thisCondition);
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
