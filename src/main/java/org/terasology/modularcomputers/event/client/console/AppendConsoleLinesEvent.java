// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.modularcomputers.event.client.console;

import org.terasology.engine.entitySystem.event.Event;
import org.terasology.engine.network.OwnerEvent;

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
