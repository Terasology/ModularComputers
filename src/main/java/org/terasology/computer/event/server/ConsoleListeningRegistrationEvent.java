// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.computer.event.server;

import org.terasology.engine.network.ServerEvent;
import org.terasology.gestalt.entitysystem.event.Event;

@ServerEvent
public class ConsoleListeningRegistrationEvent implements Event {
    private int computerId;
    private boolean register;

    public ConsoleListeningRegistrationEvent() {
    }

    public ConsoleListeningRegistrationEvent(int computerId, boolean register) {
        this.computerId = computerId;
        this.register = register;
    }

    public int getComputerId() {
        return computerId;
    }

    public boolean isRegister() {
        return register;
    }
}
