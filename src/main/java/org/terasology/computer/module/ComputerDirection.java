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
package org.terasology.computer.module;

import org.terasology.engine.math.Direction;

public class ComputerDirection {
    private ComputerDirection() {
    }

    public static Direction getDirection(String directionString) {
        if (directionString == null) {
            return null;
        }

        if (directionString.equalsIgnoreCase("up")) {
            return Direction.UP;
        } else if (directionString.equalsIgnoreCase("down")) {
            return Direction.DOWN;
        } else if (directionString.equals("north")) {
            return Direction.BACKWARD;
        } else if (directionString.equals("south")) {
            return Direction.FORWARD;
        } else if (directionString.equals("east")) {
            return Direction.RIGHT;
        } else if (directionString.equals("west")) {
            return Direction.LEFT;
        }
        return null;
    }
}
