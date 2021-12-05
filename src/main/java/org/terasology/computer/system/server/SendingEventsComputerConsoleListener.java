// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.computer.system.server;

import org.terasology.computer.context.ComputerConsoleListener;
import org.terasology.computer.event.client.console.AppendConsoleLinesEvent;
import org.terasology.computer.event.client.console.ClearConsoleScreenEvent;
import org.terasology.computer.event.client.console.SetConsoleCharactersAtEvent;
import org.terasology.computer.event.client.console.SetConsoleScreenEvent;
import org.terasology.engine.entitySystem.entity.EntityRef;

public class SendingEventsComputerConsoleListener implements ComputerConsoleListener {
    private int computerId;
    private EntityRef entityRef;

    public SendingEventsComputerConsoleListener(int computerId, EntityRef entityRef) {
        this.computerId = computerId;
        this.entityRef = entityRef;
    }

    @Override
    public void clearScreen() {
        entityRef.send(new ClearConsoleScreenEvent(computerId));
    }

    @Override
    public void setScreenState(String[] screen) {
        entityRef.send(new SetConsoleScreenEvent(computerId, screen));
    }

    @Override
    public void setCharactersStartingAt(int x, int y, String chars) {
        entityRef.send(new SetConsoleCharactersAtEvent(computerId, chars, x, y));
    }

    @Override
    public void appendLines(String[] lines) {
        entityRef.send(new AppendConsoleLinesEvent(computerId, lines));
    }
}
