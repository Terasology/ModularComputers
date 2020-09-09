// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.computer.event.client.console;

import org.terasology.engine.entitySystem.event.Event;
import org.terasology.engine.network.OwnerEvent;

@OwnerEvent
public class SetConsoleCharactersAtEvent implements Event {
    private int computerId;
    private String text;
    private int x;
    private int y;

    public SetConsoleCharactersAtEvent() {
    }

    public SetConsoleCharactersAtEvent(int computerId, String text, int x, int y) {
        this.computerId = computerId;
        this.text = text;
        this.x = x;
        this.y = y;
    }

    public int getComputerId() {
        return computerId;
    }

    public String getText() {
        return text;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
