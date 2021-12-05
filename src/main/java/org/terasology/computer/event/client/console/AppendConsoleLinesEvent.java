// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.computer.event.client.console;

import org.terasology.engine.network.OwnerEvent;
import org.terasology.gestalt.entitysystem.event.Event;

@OwnerEvent
public class AppendConsoleLinesEvent implements Event {
    private int computerId;
    private String[] lines;

    public AppendConsoleLinesEvent() {
    }

    public AppendConsoleLinesEvent(int computerId, String[] lines) {
        this.computerId = computerId;
        this.lines = lines;
    }

    public int getComputerId() {
        return computerId;
    }

    public String[] getLines() {
        return lines;
    }
}
