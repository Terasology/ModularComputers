// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.computer.event.client.console;

import org.terasology.engine.entitySystem.event.Event;
import org.terasology.engine.network.OwnerEvent;

@OwnerEvent
public class SetConsoleScreenEvent implements Event {
    private int computerId;
    private String[] screenLines;

    public SetConsoleScreenEvent() {
    }

    public SetConsoleScreenEvent(int computerId, String[] screenLines) {
        this.computerId = computerId;
        this.screenLines = screenLines;
    }

    public int getComputerId() {
        return computerId;
    }

    public String[] getScreenLines() {
        return screenLines;
    }
}
