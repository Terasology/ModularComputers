// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.computer.module.wireless;

import com.gempukku.lang.Variable;
import org.terasology.computer.context.ComputerCallback;
import org.terasology.computer.system.server.lang.os.condition.AbstractConditionCustomObject;

import java.util.Map;

public interface CommunicationChannel {
    void sendMessage(String message, ComputerCallback computer, long expireOn);
    Map<String, Variable> consumeMessage(long currentTime, ComputerCallback computer);
    AbstractConditionCustomObject consumeMessageCondition(long currentTime, ComputerCallback computer);
}
