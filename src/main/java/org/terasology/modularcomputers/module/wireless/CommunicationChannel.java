// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.modularcomputers.module.wireless;

import org.terasology.modularcomputers.context.ComputerCallback;
import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.Variable;
import org.terasology.modularcomputers.system.server.lang.os.condition.AbstractConditionCustomObject;

import java.util.Map;

public interface CommunicationChannel {
    void sendMessage(String message, ComputerCallback computer, long expireOn);

    Map<String, Variable> consumeMessage(long currentTime, ComputerCallback computer);

    AbstractConditionCustomObject consumeMessageCondition(long currentTime, ComputerCallback computer);
}
