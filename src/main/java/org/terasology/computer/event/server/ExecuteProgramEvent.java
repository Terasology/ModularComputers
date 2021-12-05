// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.computer.event.server;

import org.terasology.engine.network.ServerEvent;
import org.terasology.gestalt.entitysystem.event.Event;

@ServerEvent
public class ExecuteProgramEvent implements Event {
    private int computerId;
    private String programName;
    private String[] params;

    public ExecuteProgramEvent() {
    }

    public ExecuteProgramEvent(int computerId, String programName, String[] params) {
        this.computerId = computerId;
        this.programName = programName;
        this.params = params;
    }

    public int getComputerId() {
        return computerId;
    }

    public String getProgramName() {
        return programName;
    }

    public String[] getParams() {
        return params;
    }
}
