// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.computer.event.server;

import org.terasology.engine.entitySystem.event.Event;
import org.terasology.engine.network.ServerEvent;

@ServerEvent
public class RenameProgramEvent implements Event {
    private int computerId;
    private String programNameOld;
    private String programNameNew;

    public RenameProgramEvent() {
    }

    public RenameProgramEvent(int computerId, String programNameOld, String programNameNew) {
        this.computerId = computerId;
        this.programNameOld = programNameOld;
        this.programNameNew = programNameNew;
    }

    public int getComputerId() {
        return computerId;
    }

    public String getProgramNameOld() {
        return programNameOld;
    }

    public String getProgramNameNew() {
        return programNameNew;
    }
}
