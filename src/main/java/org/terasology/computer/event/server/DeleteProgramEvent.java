// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.computer.event.server;

import org.terasology.engine.network.ServerEvent;
import org.terasology.gestalt.entitysystem.event.Event;

@ServerEvent
public class DeleteProgramEvent implements Event {
    private int computerId;
    private String programName;

    public DeleteProgramEvent() {
    }

    public DeleteProgramEvent(int computerId, String programName) {
        this.computerId = computerId;
        this.programName = programName;
    }

    public int getComputerId() {
        return computerId;
    }

    public String getProgramName() {
        return programName;
    }
}
