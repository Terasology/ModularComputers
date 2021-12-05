// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.computer.module;

import org.terasology.engine.math.Direction;

public final class ComputerDirection {
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
