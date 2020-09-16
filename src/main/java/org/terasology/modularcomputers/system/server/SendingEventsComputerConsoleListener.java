// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.modularcomputers.system.server;

import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.modularcomputers.context.ComputerConsoleListener;
import org.terasology.modularcomputers.event.client.console.AppendConsoleLinesEvent;
import org.terasology.modularcomputers.event.client.console.ClearConsoleScreenEvent;
import org.terasology.modularcomputers.event.client.console.SetConsoleCharactersAtEvent;
import org.terasology.modularcomputers.event.client.console.SetConsoleScreenEvent;

public class SendingEventsComputerConsoleListener implements ComputerConsoleListener {
    private final int computerId;
    private final EntityRef entityRef;

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
