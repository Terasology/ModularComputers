// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.computer.ui;

import org.terasology.computer.context.ComputerConsole;
import org.terasology.nui.Canvas;
import org.terasology.nui.Color;
import org.terasology.input.Keyboard;

import java.util.LinkedList;
import java.util.List;

public class PlayerCommandConsoleGui {
    public static final Color PLAYER_CONSOLE_TEXT_COLOR = new Color(0xffffffff);
    public static final Color COMMAND_LINE_TEXT_COLOR = new Color(0xffff99ff);
    public static final Color PLAYER_CONSOLE_CURSOR_COLOR = new Color(0xff0000ff);
    private static final int BLINK_LENGTH = 20;

    private ComputerConsole playerConsole = new ComputerConsole();
    private StringBuilder currentCommand = new StringBuilder();
    private int cursorPositionInPlayerCommand = 0;
    private int currentCommandDisplayStartIndex = 0;

    private int blinkDrawTick;

    private boolean readOnly;

    private ComputerTerminalWidget computerTerminalWidget;

    private int historyIndex = 0;
    private List<String> commandHistory = new LinkedList<>();

    public PlayerCommandConsoleGui(ComputerTerminalWidget computerTerminalWidget) {
        this.computerTerminalWidget = computerTerminalWidget;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    public void drawPlayerCommandConsole(Canvas canvas, boolean focused, int x, int y, int characterWidth, int fontHeight) {
        final String[] consoleLines = playerConsole.getLines();
        // Draw all lines but first (we need to fill current edited line at the bottom)
        for (int i = 1; i < consoleLines.length; i++) {
            computerTerminalWidget.drawMonospacedText(canvas, consoleLines[i], x, y + (i - 1) * fontHeight, PLAYER_CONSOLE_TEXT_COLOR);
        }

        if (!readOnly) {
            String wholeCommandLine = ">" + currentCommand.toString();
            String commandLine = wholeCommandLine.substring(currentCommandDisplayStartIndex, Math.min(currentCommandDisplayStartIndex +
                    ComputerConsole.CONSOLE_WIDTH, wholeCommandLine.length()));
            int cursorPositionInDisplayedCommandLine = 1 + cursorPositionInPlayerCommand - currentCommandDisplayStartIndex;

            final int lastLineY = y + fontHeight * (ComputerConsole.CONSOLE_HEIGHT - 1);
            computerTerminalWidget.drawMonospacedText(canvas, commandLine, x, lastLineY, COMMAND_LINE_TEXT_COLOR);

            if (focused) {
                blinkDrawTick = ((++blinkDrawTick) % BLINK_LENGTH);
                if (blinkDrawTick * 2 > BLINK_LENGTH) {
                    computerTerminalWidget.drawVerticalLine(canvas, x + cursorPositionInDisplayedCommandLine * characterWidth - 1,
                            1 + lastLineY, lastLineY + fontHeight, PLAYER_CONSOLE_CURSOR_COLOR);
                }
            }
        }
    }

    public void keyTypedInPlayerConsole(char character, int keyboardCharId) {
        if (!readOnly) {
            if (character >= 32 && character < 127) {
                currentCommand.insert(cursorPositionInPlayerCommand, character);
                cursorPositionInPlayerCommand++;
            } else if (keyboardCharId == Keyboard.KeyId.BACKSPACE && cursorPositionInPlayerCommand > 0) {
                currentCommand.delete(cursorPositionInPlayerCommand - 1, cursorPositionInPlayerCommand);
                cursorPositionInPlayerCommand--;
            } else if (keyboardCharId == Keyboard.KeyId.DELETE && cursorPositionInPlayerCommand < currentCommand.length()) {
                currentCommand.delete(cursorPositionInPlayerCommand, cursorPositionInPlayerCommand + 1);
            } else if (keyboardCharId == Keyboard.KeyId.LEFT && cursorPositionInPlayerCommand > 0) {
                cursorPositionInPlayerCommand--;
            } else if (keyboardCharId == Keyboard.KeyId.RIGHT && cursorPositionInPlayerCommand < currentCommand.length()) {
                cursorPositionInPlayerCommand++;
            } else if (keyboardCharId == Keyboard.KeyId.HOME) {
                cursorPositionInPlayerCommand = 0;
            } else if (keyboardCharId == Keyboard.KeyId.END) {
                cursorPositionInPlayerCommand = currentCommand.length();
            } else if (keyboardCharId == Keyboard.KeyId.ENTER) {
                String command = currentCommand.toString().trim();
                if (command.length() > 0) {
                    appendToHistory(command);
                    playerConsole.appendString(">" + command);

                    computerTerminalWidget.executeCommand(command);
                    currentCommand = new StringBuilder();
                    cursorPositionInPlayerCommand = 0;
                }
            } else if (keyboardCharId == Keyboard.KeyId.UP) {
                if (historyIndex < commandHistory.size()) {
                    currentCommand = new StringBuilder();
                    currentCommand.append(commandHistory.get(historyIndex));
                    cursorPositionInPlayerCommand = currentCommand.length();
                    historyIndex++;
                }
            } else if (keyboardCharId == Keyboard.KeyId.DOWN) {
                if (historyIndex > 1) {
                    currentCommand = new StringBuilder();
                    currentCommand.append(commandHistory.get(historyIndex - 2));
                    cursorPositionInPlayerCommand = currentCommand.length();
                    historyIndex--;
                } else if (historyIndex == 1) {
                    currentCommand = new StringBuilder();
                    cursorPositionInPlayerCommand = 0;
                    historyIndex = 0;
                }
            }

            // Adjust start position
            int lineLength = currentCommand.length() + 1;
            if (lineLength <= ComputerConsole.CONSOLE_WIDTH) {
                currentCommandDisplayStartIndex = 0;
            } else {
                int cursorPositionInCommand = 1 + cursorPositionInPlayerCommand;
                if (currentCommandDisplayStartIndex + ComputerConsole.CONSOLE_WIDTH > lineLength) {
                    currentCommandDisplayStartIndex = lineLength - ComputerConsole.CONSOLE_WIDTH;
                } else if (cursorPositionInCommand > ComputerConsole.CONSOLE_WIDTH + currentCommandDisplayStartIndex) {
                    currentCommandDisplayStartIndex = cursorPositionInCommand - ComputerConsole.CONSOLE_WIDTH;
                } else if (cursorPositionInCommand - 1 < currentCommandDisplayStartIndex) {
                    currentCommandDisplayStartIndex = cursorPositionInCommand - 1;
                }
            }
        }
    }

    private void appendToHistory(String command) {
        commandHistory.add(0, command);
        historyIndex = 0;
        if (commandHistory.size() > 20) {
            commandHistory.remove(20);
        }
    }

    public void appendToConsole(String text) {
        playerConsole.appendString(text);
    }
}
