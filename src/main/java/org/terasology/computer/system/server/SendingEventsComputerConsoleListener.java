/*
 * Copyright 2015 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
