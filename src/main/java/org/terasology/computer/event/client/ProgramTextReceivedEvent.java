// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.computer.event.client;

import org.terasology.engine.network.OwnerEvent;
import org.terasology.gestalt.entitysystem.event.Event;

@OwnerEvent
public class ProgramTextReceivedEvent implements Event {
    private int computerId;
    private String programName;
    private String programText;

    public ProgramTextReceivedEvent() {
    }

    public ProgramTextReceivedEvent(int computerId, String programName, String programText) {
        this.computerId = computerId;
        this.programName = programName;
        this.programText = programText;
    }

    public int getComputerId() {
        return computerId;
    }

    public String getProgramName() {
        return programName;
    }

    public String getProgramText() {
        return programText;
    }
}
