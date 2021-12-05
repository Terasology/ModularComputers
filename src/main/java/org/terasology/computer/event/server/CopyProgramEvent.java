// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.computer.event.server;

import org.terasology.engine.network.ServerEvent;
import org.terasology.gestalt.entitysystem.event.Event;

@ServerEvent
public class CopyProgramEvent implements Event {
    private int computerId;
    private String programNameSource;
    private String programNameDestination;

    public CopyProgramEvent() {
    }

    public CopyProgramEvent(int computerId, String programNameSource, String programNameDestination) {
        this.computerId = computerId;
        this.programNameSource = programNameSource;
        this.programNameDestination = programNameDestination;
    }

    public int getComputerId() {
        return computerId;
    }

    public String getProgramNameSource() {
        return programNameSource;
    }

    public String getProgramNameDestination() {
        return programNameDestination;
    }
}
