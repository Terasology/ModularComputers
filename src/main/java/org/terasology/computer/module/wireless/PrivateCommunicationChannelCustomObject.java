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

import com.gempukku.lang.CustomObject;
import com.gempukku.lang.ExecutionException;
import com.gempukku.lang.Variable;
import org.joml.RoundingMode;
import org.joml.Vector3i;
import org.terasology.computer.context.ComputerCallback;
import org.terasology.computer.system.server.lang.os.condition.AbstractConditionCustomObject;
import org.terasology.entitySystem.entity.EntityRef;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class PrivateCommunicationChannelCustomObject implements CustomObject, CommunicationChannel {
    private String channelName;
    private float range;
    private CommunicationChannels<EntityRef> communicationChannels;

    public PrivateCommunicationChannelCustomObject(String channelName, float range, CommunicationChannels<EntityRef> communicationChannels) {
        this.channelName = channelName;
        this.range = range;
        this.communicationChannels = communicationChannels;
    }

    @Override
    public void sendMessage(String message, ComputerCallback computer, long expireOn) {
        communicationChannels.addPrivateMessage(channelName, computer.getExecutedBy(), new Vector3i(computer.getComputerLocation(), RoundingMode.FLOOR), range, message, expireOn);
    }

    @Override
    public Map<String, Variable> consumeMessage(long currentTime, ComputerCallback computer) {
        return communicationChannels.consumeNextPrivateMessage(currentTime, channelName, computer.getExecutedBy(), new Vector3i(computer.getComputerLocation(), RoundingMode.FLOOR), range);
    }

    @Override
    public AbstractConditionCustomObject consumeMessageCondition(long currentTime, ComputerCallback computer) {
        return new MessageAwaitingLatchCondition(new Vector3i(computer.getComputerLocation(), RoundingMode.FLOOR), range) {
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
                            communicationChannels.removePrivateMessageCondition(channelName, computer.getExecutedBy(), thisCondition);
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
