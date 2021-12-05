// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.computer.context;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ComputerConsole {
    public static final int CONSOLE_WIDTH = 87;
    public static final int CONSOLE_HEIGHT = 35;

    private Set<ComputerConsoleListener> consoleListeners = new HashSet<ComputerConsoleListener>();

    // Please note, it's addressable by chars[y][x] to allow easy creation of Strings based on line index
    private char[][] chars = new char[CONSOLE_HEIGHT][CONSOLE_WIDTH];

    public void addConsoleListener(ComputerConsoleListener listener) {
        consoleListeners.add(listener);
        // Send the state immediately to the client
        String[] screen = new String[CONSOLE_HEIGHT];
        for (int i = 0; i < screen.length; i++) {
            screen[i] = getLine(i);
        }
        listener.setScreenState(screen);
    }

    public void removeConsoleListener(ComputerConsoleListener listener) {
        consoleListeners.remove(listener);
    }

    public String[] getLines() {
        String[] result = new String[CONSOLE_HEIGHT];
        for (int i = 0; i < CONSOLE_HEIGHT; i++) {
            result[i] = getLine(i);
        }
        return result;
    }

    public void clearConsole() {
        for (int i = 0; i < CONSOLE_HEIGHT; i++) {
            chars[i] = new char[CONSOLE_WIDTH];
        }

        for (ComputerConsoleListener listener : consoleListeners) {
            listener.clearScreen();
        }
    }

    // This method should not be called by the program directly, only for internal use
    public void setConsoleState(String[] lines) {
        for (int i = 0; i < lines.length; i++) {
            lines[i] = stripInvalidCharacters(lines[i]);
            setLine(i, lines[i]);
        }

        for (ComputerConsoleListener listener : consoleListeners) {
            listener.setScreenState(lines);
        }
    }

    public void setCharacters(int x, int y, String text) {
        if (y >= 0 || y < CONSOLE_HEIGHT) {
            String textToUse = stripInvalidCharacters(text);
            textToUse = textToUse.substring(0, Math.min(textToUse.length(), CONSOLE_WIDTH - x));
            if (textToUse.length() > 0) {
                final char[] charactersToSet = textToUse.toCharArray();
                System.arraycopy(charactersToSet, 0, this.chars[y], x, charactersToSet.length);

                for (ComputerConsoleListener listener : consoleListeners) {
                    listener.setCharactersStartingAt(x, y, new String(charactersToSet));
                }
            }
        }
    }

    public void appendString(String toAppend) {
        final String[] lines = toAppend.split("\n");
        List<String> realLinesToAppend = new ArrayList<>();
        for (String line : lines) {
            final String printableLine = stripInvalidCharacters(line);
            for (int i = 0; i < printableLine.length(); i += CONSOLE_WIDTH) {
                realLinesToAppend.add(printableLine.substring(i, Math.min(i + CONSOLE_WIDTH, printableLine.length())));
            }
        }
        // Strip all the lines that are overflowing the screen
        if (realLinesToAppend.size() > CONSOLE_HEIGHT) {
            realLinesToAppend = realLinesToAppend.subList(realLinesToAppend.size() - CONSOLE_HEIGHT, realLinesToAppend.size());
        }

        String[] realLines = realLinesToAppend.toArray(new String[realLinesToAppend.size()]);

        // Move all existing lines up, unless we need to replace all lines
        if (realLines.length < CONSOLE_HEIGHT) {
            System.arraycopy(chars, realLines.length, chars, 0, CONSOLE_HEIGHT - realLines.length);
        }

        // Replace the lines (at the end) with the contents of realLines
        int startIndex = CONSOLE_HEIGHT - realLines.length;
        for (int i = startIndex; i < CONSOLE_HEIGHT; i++) {
            setLine(i, realLines[i - startIndex]);
        }

        // Notify listeners
        for (ComputerConsoleListener consoleListener : consoleListeners) {
            consoleListener.appendLines(realLines);
        }
    }

    private void setLine(int lineIndex, String text) {
        chars[lineIndex] = new char[CONSOLE_WIDTH];
        System.arraycopy(text.toCharArray(), 0, chars[lineIndex], 0, text.length());
    }

    private String getLine(int lineIndex) {
        final char[] lineChars = chars[lineIndex];

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < CONSOLE_WIDTH; i++) {
            if (lineChars[i] == 0) {
                result.append((char) 32);
            } else {
                result.append(lineChars[i]);
            }
        }
        return result.toString();
    }

    public static String stripInvalidCharacters(String text) {
        StringBuilder result = new StringBuilder();
        final char[] chars = text.toCharArray();
        for (char aChar : chars) {
            if (aChar >= 32 && aChar <= 126) {
                result.append(aChar);
            }
        }

        return result.toString();
    }
}
