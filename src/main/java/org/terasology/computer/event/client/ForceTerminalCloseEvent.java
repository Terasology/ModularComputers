// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.computer.event.client;

import org.terasology.engine.entitySystem.event.Event;
import org.terasology.engine.network.OwnerEvent;

@OwnerEvent
public class ForceTerminalCloseEvent implements Event {
    private int computerId;

    public ForceTerminalCloseEvent() {
    }

    public ForceTerminalCloseEvent(int computerId) {
        this.computerId = computerId;
    }

    public int getComputerId() {
        return computerId;
    }
}
