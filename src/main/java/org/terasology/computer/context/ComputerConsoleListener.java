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
package org.terasology.computer.context;

public interface ComputerConsoleListener {
    /**
     * Clears the screen.
     */
    public void clearScreen();

    /**
     * Sets the state of the console screen to display the following lines.
     *
     * @param screen
     */
    public void setScreenState(String[] screen);

    /**
     * Replaces characters at the specified x,y
     *
     * @param x
     * @param y
     * @param chars
     */
    public void setCharactersStartingAt(int x, int y, String chars);

    /**
     * Appends the following lines to the console, moving anything that is on screen already to the top as many lines,
     * as many lines are getting appended.
     *
     * @param lines
     */
    public void appendLines(String[] lines);
}