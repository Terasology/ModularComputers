// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.computer.event.client;

import org.terasology.engine.entitySystem.event.Event;
import org.terasology.engine.network.OwnerEvent;

@OwnerEvent
public class ProgramExecutionResultEvent implements Event {
    private int computerId;
    private String message;

    public ProgramExecutionResultEvent() {
    }

    public ProgramExecutionResultEvent(int computerId, String message) {
        this.computerId = computerId;
        this.message = message;
    }

    public int getComputerId() {
        return computerId;
    }

    public String getMessage() {
        return message;
    }
}
