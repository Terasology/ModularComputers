// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.computer.event.client.console;

import org.terasology.engine.network.OwnerEvent;
import org.terasology.gestalt.entitysystem.event.Event;

@OwnerEvent
public class ClearConsoleScreenEvent implements Event {
    private int computerId;

    public ClearConsoleScreenEvent() {

    }

    public ClearConsoleScreenEvent(int computerId) {
        this.computerId = computerId;
    }

    public int getComputerId() {
        return computerId;
    }
}
