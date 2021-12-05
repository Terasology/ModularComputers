// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.computer.event.server;

import org.terasology.engine.network.ServerEvent;
import org.terasology.gestalt.entitysystem.event.Event;

@ServerEvent
public class ListProgramsEvent implements Event {
    private int computerId;

    public ListProgramsEvent() {
    }

    public ListProgramsEvent(int computerId) {
        this.computerId = computerId;
    }

    public int getComputerId() {
        return computerId;
    }
}
