// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.computer.context;

public interface ComputerConsoleListener {
    /**
     * Clears the screen.
     */
    void clearScreen();

    /**
     * Sets the state of the console screen to display the following lines.
     *
     * @param screen
     */
    void setScreenState(String[] screen);

    /**
     * Replaces characters at the specified x,y
     *
     * @param x
     * @param y
     * @param chars
     */
    void setCharactersStartingAt(int x, int y, String chars);

    /**
     * Appends the following lines to the console, moving anything that is on screen already to the top as many lines,
     * as many lines are getting appended.
     *
     * @param lines
     */
    void appendLines(String[] lines);
}
