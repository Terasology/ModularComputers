// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.computer.event.server;

import org.terasology.engine.entitySystem.event.Event;
import org.terasology.engine.network.ServerEvent;

@ServerEvent
public class StopProgramEvent implements Event {
    private int computerId;

    public StopProgramEvent() {
    }

    public StopProgramEvent(int computerId) {
        this.computerId = computerId;
    }

    public int getComputerId() {
        return computerId;
    }
}
