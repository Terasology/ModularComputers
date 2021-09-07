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
package org.terasology.computer.event.server;

import org.terasology.engine.network.ServerEvent;
import org.terasology.gestalt.entitysystem.event.Event;

@ServerEvent
public class ExecuteProgramEvent implements Event {
    private int computerId;
    private String programName;
    private String[] params;

    public ExecuteProgramEvent() {
    }

    public ExecuteProgramEvent(int computerId, String programName, String[] params) {
        this.computerId = computerId;
        this.programName = programName;
        this.params = params;
    }

    public int getComputerId() {
        return computerId;
    }

    public String getProgramName() {
        return programName;
    }

    public String[] getParams() {
        return params;
    }
}
