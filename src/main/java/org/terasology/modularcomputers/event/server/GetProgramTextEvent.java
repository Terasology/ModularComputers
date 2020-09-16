// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.modularcomputers.event.server;

import org.terasology.engine.entitySystem.event.Event;
import org.terasology.engine.network.ServerEvent;

@ServerEvent
public class GetProgramTextEvent implements Event {
    private int computerId;
    private String programName;

    public GetProgramTextEvent() {
    }

    public GetProgramTextEvent(int computerId, String programName) {
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
