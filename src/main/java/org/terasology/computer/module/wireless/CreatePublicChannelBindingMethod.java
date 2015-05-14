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

import com.gempukku.lang.ExecutionException;
import com.gempukku.lang.Variable;
import org.terasology.computer.FunctionParamValidationUtil;
import org.terasology.computer.context.ComputerCallback;
import org.terasology.computer.system.server.lang.AbstractModuleMethodExecutable;
import org.terasology.entitySystem.entity.EntityRef;

import java.util.Map;

public class CreatePublicChannelBindingMethod extends AbstractModuleMethodExecutable<Object> {
    private String methodName;
    private CommunicationChannels<EntityRef> communicationChannels;
    private float range;

    public CreatePublicChannelBindingMethod(String methodName, CommunicationChannels<EntityRef> communicationChannels, float range) {
        super("Creates a public channel binding to send to and receive messages from.", "CommunicationChannelBinding", "Communication channel binding requested.");
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
    public Object onFunctionEnd(int line, ComputerCallback computer, Map<String, Variable> parameters, Object onFunctionStartResult) throws ExecutionException {
        String channelName = FunctionParamValidationUtil.validateStringParameter(line, parameters, "channelName", methodName);
        return new PublicCommunicationChannelCustomObject(channelName, range, communicationChannels);
    }
}
