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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ComputerConsole {
    private Set<ComputerConsoleListener> _consoleListeners = new HashSet<ComputerConsoleListener>();
    public static final int CONSOLE_WIDTH = 87;
    public static final int CONSOLE_HEIGHT = 35;

    // Please note, it's addressable by _chars[y][x] to allow easy creation of Strings based on line index
    private char[][] _chars = new char[CONSOLE_HEIGHT][CONSOLE_WIDTH];

    public void addConsoleListener(ComputerConsoleListener listener) {
        _consoleListeners.add(listener);
        // Send the state immediately to the client
        String[] screen = new String[CONSOLE_HEIGHT];
        for (int i = 0; i < screen.length; i++)
            screen[i] = getLine(i);
        listener.setScreenState(screen);
    }

    public void removeConsoleListener(ComputerConsoleListener listener) {
        _consoleListeners.remove(listener);
    }

    public String[] getLines() {
        String[] result = new String[CONSOLE_HEIGHT];
        for (int i = 0; i < CONSOLE_HEIGHT; i++)
            result[i] = getLine(i);
        return result;
    }

    public void clearConsole() {
        for (int i = 0; i < CONSOLE_HEIGHT; i++)
            _chars[i] = new char[CONSOLE_WIDTH];

        for (ComputerConsoleListener listener : _consoleListeners)
            listener.clearScreen();
    }

    // This method should not be called by the program directly, only for internal use
    public void setConsoleState(String[] lines) {
        for (int i = 0; i < lines.length; i++) {
            lines[i] = stripInvalidCharacters(lines[i]);
            setLine(i, lines[i]);
        }

        for (ComputerConsoleListener listener : _consoleListeners)
            listener.setScreenState(lines);
    }

    public void setCharacters(int x, int y, String text) {
        if (y >= 0 || y < CONSOLE_HEIGHT) {
            text = stripInvalidCharacters(text);
            text = text.substring(0, Math.min(text.length(), CONSOLE_WIDTH - x));
            if (text.length() > 0) {
                final char[] chars = text.toCharArray();
                System.arraycopy(chars, 0, _chars[y], x, chars.length);

                for (ComputerConsoleListener listener : _consoleListeners)
                    listener.setCharactersStartingAt(x, y, new String(chars));
            }
        }
    }

    public void appendString(String toAppend) {
        final String[] lines = toAppend.split("\n");
        List<String> realLinesToAppend = new ArrayList<String>();
        for (String line : lines) {
            final String printableLine = stripInvalidCharacters(line);
            for (int i = 0; i < printableLine.length(); i += CONSOLE_WIDTH)
                realLinesToAppend.add(printableLine.substring(i, Math.min(i + CONSOLE_WIDTH, printableLine.length())));
        }
        // Strip all the lines that are overflowing the screen
        if (realLinesToAppend.size() > CONSOLE_HEIGHT)
            realLinesToAppend = realLinesToAppend.subList(realLinesToAppend.size() - CONSOLE_HEIGHT, realLinesToAppend.size());

        String[] realLines = realLinesToAppend.toArray(new String[realLinesToAppend.size()]);

        // Move all existing lines up, unless we need to replace all lines
        if (realLines.length < CONSOLE_HEIGHT)
            System.arraycopy(_chars, realLines.length, _chars, 0, CONSOLE_HEIGHT - realLines.length);

        // Replace the lines (at the end) with the contents of realLines
        int startIndex = CONSOLE_HEIGHT - realLines.length;
        for (int i = startIndex; i < CONSOLE_HEIGHT; i++)
            setLine(i, realLines[i - startIndex]);

        // Notify listeners
        for (ComputerConsoleListener consoleListener : _consoleListeners)
            consoleListener.appendLines(realLines);
    }

    private void setLine(int lineIndex, String text) {
        _chars[lineIndex] = new char[CONSOLE_WIDTH];
        System.arraycopy(text.toCharArray(), 0, _chars[lineIndex], 0, text.length());
    }

    private String getLine(int lineIndex) {
        final char[] lineChars = _chars[lineIndex];

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < CONSOLE_WIDTH; i++)
            if (lineChars[i] == 0)
                result.append((char) 32);
            else
                result.append(lineChars[i]);
        return result.toString();
    }

    public static String stripInvalidCharacters(String text) {
        StringBuilder result = new StringBuilder();
        final char[] chars = text.toCharArray();
        for (char aChar : chars)
            if (aChar >= 32 && aChar <= 126)
                result.append(aChar);

        return result.toString();
    }
}