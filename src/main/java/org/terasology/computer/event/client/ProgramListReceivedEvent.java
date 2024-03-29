// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.computer.event.client;

import org.terasology.engine.network.OwnerEvent;
import org.terasology.gestalt.entitysystem.event.Event;

import java.util.Collection;

@OwnerEvent
public class ProgramListReceivedEvent implements Event {
    private int computerId;
    private Collection<String> programs;

    public ProgramListReceivedEvent() {
    }

    public ProgramListReceivedEvent(int computerId, Collection<String> programs) {
        this.computerId = computerId;
        this.programs = programs;
    }

    public int getComputerId() {
        return computerId;
    }

    public Collection<String> getPrograms() {
        return programs;
    }
}
