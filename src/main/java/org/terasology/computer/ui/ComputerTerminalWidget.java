// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.computer.ui;

import org.joml.Rectanglei;
import org.joml.Vector2i;
import org.terasology.computer.context.ComputerConsole;
import org.terasology.computer.event.server.ConsoleListeningRegistrationEvent;
import org.terasology.computer.event.server.CopyProgramEvent;
import org.terasology.computer.event.server.DeleteProgramEvent;
import org.terasology.computer.event.server.ExecuteProgramEvent;
import org.terasology.computer.event.server.GetProgramTextEvent;
import org.terasology.computer.event.server.ListProgramsEvent;
import org.terasology.computer.event.server.RenameProgramEvent;
import org.terasology.computer.event.server.SaveProgramEvent;
import org.terasology.computer.event.server.StopProgramEvent;
import org.terasology.computer.system.common.ComputerLanguageContextInitializer;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.input.Keyboard;
import org.terasology.input.device.KeyboardDevice;
import org.terasology.logic.characters.CharacterComponent;
import org.terasology.logic.clipboard.ClipboardManager;
import org.terasology.logic.common.DisplayNameComponent;
import org.terasology.math.JomlUtil;
import org.terasology.network.ClientComponent;
import org.terasology.nui.Canvas;
import org.terasology.nui.Color;
import org.terasology.nui.CoreWidget;
import org.terasology.nui.HorizontalAlign;
import org.terasology.nui.LayoutConfig;
import org.terasology.nui.VerticalAlign;
import org.terasology.nui.events.NUICharEvent;
import org.terasology.nui.events.NUIKeyEvent;
import org.terasology.rendering.assets.font.Font;
import org.terasology.utilities.Assets;

import java.util.Collection;

public class ComputerTerminalWidget extends CoreWidget {
    public static final Color BACKGROUND_COLOR = new Color(0x111111ff);
    public static final Color FRAME_COLOR = new Color(0xffffffff);

    public static final Color COMPUTER_CONSOLE_TEXT_COLOR = new Color(0xffffffff);

    private static final int PADDING_HOR = 5;
    private static final int PADDING_VER = 5;

    @LayoutConfig
    private String monospaceFont;

    @LayoutConfig
    private int characterWidth;

    @LayoutConfig
    private int fontHeight;

    private Font monospacedFont;

    private TerminalMode mode = TerminalMode.PLAYER_CONSOLE;
    private boolean editingProgram;

    private ComputerConsole computerConsole;

    private Runnable closeRunnable;
    private EntityRef clientEntity;
    private int computerId;

    private PlayerCommandConsoleGui playerCommandConsoleGui;
    private ProgramEditingConsoleGui programEditingConsoleGui;

    public void setup(ComputerLanguageContextInitializer computerLanguageContextInitializer, ClipboardManager clipboardManager,
                      Runnable closeRunnable, EntityRef clientEntity, int computerId) {
        editingProgram = false;
        computerConsole = new ComputerConsole();

        this.closeRunnable = closeRunnable;
        this.clientEntity = clientEntity;
        this.computerId = computerId;

        playerCommandConsoleGui = new PlayerCommandConsoleGui(this);
        playerCommandConsoleGui.appendToConsole("AutomationOS v. 0.0");
        String userName = clientEntity.getComponent(CharacterComponent.class).controller.getComponent(ClientComponent.class).clientInfo.getComponent(DisplayNameComponent.class).name;
        playerCommandConsoleGui.appendToConsole("You're logged in as " + userName + ", use \"exit\" command to exit the console, use \"help\" to list commands.");
        programEditingConsoleGui = new ProgramEditingConsoleGui(this, computerLanguageContextInitializer, clipboardManager);

        this.clientEntity.send(new ConsoleListeningRegistrationEvent(this.computerId, true));
    }

    public void saveProgram(String programName, String code) {
        clientEntity.send(new SaveProgramEvent(computerId, programName, code));
    }

    public int getComputerId() {
        return computerId;
    }

    public void setMode(TerminalMode mode) {
        this.mode = mode;
    }

    public void onClosed() {
        clientEntity.send(new ConsoleListeningRegistrationEvent(computerId, false));
    }

    public void appendToPlayerConsole(String text) {
        playerCommandConsoleGui.appendToConsole(text);
    }

    public void displayProgramList(Collection<String> programs) {
        if (!programs.isEmpty()) {
            StringBuilder allPrograms = new StringBuilder();
            for (Object program : programs) {
                allPrograms.append(program).append(" ");
            }
            playerCommandConsoleGui.appendToConsole(allPrograms.toString().trim());
        }
        playerCommandConsoleGui.setReadOnly(false);
    }

    @Override
    public void onDraw(Canvas canvas) {
        Rectanglei region = canvas.getRegion();

        int screenWidth = region.lengthX();
        int screenHeight = region.lengthY();

        // Fill background with solid dark-grey
        canvas.drawFilledRectangle(JomlUtil.rectangleiFromMinAndSize(0, 0, screenWidth, screenHeight), BACKGROUND_COLOR);

        // Draw white rectangle around the screen
        drawHorizontalLine(canvas, 0, 0, screenWidth, FRAME_COLOR);
        drawHorizontalLine(canvas, 0, screenHeight, screenWidth, FRAME_COLOR);
        drawVerticalLine(canvas, 0, 0, screenHeight, FRAME_COLOR);
        drawVerticalLine(canvas, screenWidth, 0, screenHeight, FRAME_COLOR);


        if (mode == TerminalMode.PLAYER_CONSOLE) {
            drawPlayerConsole(canvas, isFocused());
        } else if (mode == TerminalMode.COMPUTER_CONSOLE) {
            drawComputerConsole(canvas);
        }
    }

    private Font getFont(Canvas canvas) {
        if (monospacedFont == null) {
            monospacedFont = Assets.getFont(monospaceFont).get();
        }
        return monospacedFont;
    }

    @Override
    public Vector2i getPreferredContentSize(Canvas canvas, Vector2i sizeHint) {
        int width = PADDING_HOR * 2 + characterWidth * ComputerConsole.CONSOLE_WIDTH;
        int height = PADDING_VER * 2 + fontHeight * ComputerConsole.CONSOLE_HEIGHT;
        return new Vector2i(width, height);
    }

    protected void requestSave(String programName, String programText) {
        clientEntity.send(new SaveProgramEvent(computerId, programName, programText));
    }

    private void drawPlayerConsole(Canvas canvas, boolean focused) {
        if (editingProgram) {
            programEditingConsoleGui.drawEditProgramConsole(canvas, focused, PADDING_HOR, PADDING_VER, characterWidth, fontHeight);
        } else {
            playerCommandConsoleGui.drawPlayerCommandConsole(canvas, focused, PADDING_HOR, PADDING_VER, characterWidth, fontHeight);
        }
    }

    private void drawComputerConsole(Canvas canvas) {
        final String[] consoleLines = computerConsole.getLines();
        for (int i = 0; i < consoleLines.length; i++) {
            drawMonospacedText(canvas, consoleLines[i], PADDING_HOR, PADDING_VER + i * fontHeight, COMPUTER_CONSOLE_TEXT_COLOR);
        }
    }

    public void drawVerticalLine(Canvas canvas, int x, int y1, int y2, Color color) {
        canvas.drawLine(x, y1, x, y2, color);
    }

    public void drawHorizontalLine(Canvas canvas, int x1, int y, int x2, Color color) {
        canvas.drawLine(x1, y, x2, y, color);
    }

    public interface Coloring {
        public Color getColor(int column);
    }

    protected void drawMonospacedText(Canvas canvas, String text, int x, int y, Coloring coloring) {
        char[] chars = text.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            Color color = coloring.getColor(i);
            renderCharAt(canvas, chars[i], x + i * characterWidth, y, color);
        }
    }

    protected void drawMonospacedText(Canvas canvas, String text, int x, int y, Color color) {
        char[] chars = text.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            renderCharAt(canvas, chars[i], x + i * characterWidth, y, color);
        }
    }

    private void renderCharAt(Canvas canvas, char ch, int x, int y, Color color) {
        canvas.drawTextRaw(String.valueOf(ch), getFont(canvas), color, JomlUtil.rectangleiFromMinAndSize(x, y, characterWidth, fontHeight), HorizontalAlign.CENTER, VerticalAlign.TOP);
    }

    @Override
    public boolean onKeyEvent(NUIKeyEvent event) {
        int keyboardCharId = event.getKey().getId();
        if (mode == TerminalMode.PLAYER_CONSOLE) {
            if (editingProgram) {
                KeyboardDevice keyboard = event.getKeyboard();
                programEditingConsoleGui.keyTypedInEditingProgram(keyboardCharId,
                        keyboard.isKeyDown(Keyboard.KeyId.LEFT_CTRL) || keyboard.isKeyDown(Keyboard.KeyId.RIGHT_CTRL));
            } else {
                playerCommandConsoleGui.keyTypedInPlayerConsole(keyboardCharId);
            }
        }
        if (event.getKey().getId() != Keyboard.KeyId.ESCAPE) {
            return true;
        }

        return false;
    }

    @Override
    public boolean onCharEvent(NUICharEvent event) {
        char character = event.getCharacter();
        if (mode == TerminalMode.PLAYER_CONSOLE) {
            if (editingProgram) {
                KeyboardDevice keyboard = event.getKeyboard();
                programEditingConsoleGui.charTypedInEditinProgram(character,
                        keyboard.isKeyDown(Keyboard.KeyId.LEFT_CTRL) || keyboard.isKeyDown(Keyboard.KeyId.RIGHT_CTRL));
            } else {
                playerCommandConsoleGui.charTypedInPlayerConsole(character);
            }
        }
        return false;
    }

    protected void exitProgramming() {
        editingProgram = false;
    }

    protected void executeCommand(String command) {
        String[] commandParts = command.split(" ");
        if (commandParts.length > 0) {
            if (commandParts[0].equals("exit")) {
                closeRunnable.run();
            } else if (commandParts[0].equals("help")) {
                printHelp();
            } else if (commandParts[0].equals("edit")) {
                if (commandParts.length != 2) {
                    playerCommandConsoleGui.appendToConsole("Usage:");
                    playerCommandConsoleGui.appendToConsole("edit [programName] - edits or creates a new program with the specified name");
                } else if (!isValidProgramName(commandParts[1])) {
                    playerCommandConsoleGui.appendToConsole("Invalid program name - only letters and digits allowed and a maximum length of 10");
                } else {
                    String programName = commandParts[1];

                    playerCommandConsoleGui.appendToConsole("Downloading program...");

                    playerCommandConsoleGui.setReadOnly(true);
                    clientEntity.send(new GetProgramTextEvent(computerId, programName));
                }
            } else if (commandParts[0].equals("execute")) {
                if (commandParts.length < 2) {
                    playerCommandConsoleGui.appendToConsole("Usage:");
                    playerCommandConsoleGui.appendToConsole("execute [programName] [argument] ... [argument]- executes specified program with specified arguments (if any)");
                } else if (!isValidProgramName(commandParts[1])) {
                    playerCommandConsoleGui.appendToConsole("Invalid program name - only letters and digits allowed and a maximum length of 10");
                } else {
                    String[] arguments = new String[commandParts.length - 2];
                    for (int i = 0; i < commandParts.length - 2; i++) {
                        arguments[i] = commandParts[i + 2];
                    }
                    clientEntity.send(new ExecuteProgramEvent(computerId, commandParts[1], arguments));
                }
            } else if (commandParts[0].equals("list")) {
                if (commandParts.length > 1) {
                    playerCommandConsoleGui.appendToConsole("Usage:");
                    playerCommandConsoleGui.appendToConsole("list - lists all programs on that computer");
                } else {
                    playerCommandConsoleGui.appendToConsole("Retrieving list of programs...");

                    playerCommandConsoleGui.setReadOnly(true);
                    clientEntity.send(new ListProgramsEvent(computerId));
                }
            } else if (commandParts[0].equals("delete")) {
                if (commandParts.length != 2) {
                    playerCommandConsoleGui.appendToConsole("Usage:");
                    playerCommandConsoleGui.appendToConsole("delete [programName] - deletes specified program");
                } else if (!isValidProgramName(commandParts[1])) {
                    playerCommandConsoleGui.appendToConsole("Invalid program name - only letters and digits allowed and a maximum length of 10");
                } else {
                    clientEntity.send(new DeleteProgramEvent(computerId, commandParts[1]));
                }
            } else if (commandParts[0].equals("copy")) {
                if (commandParts.length != 3) {
                    playerCommandConsoleGui.appendToConsole("Usage:");
                    playerCommandConsoleGui.appendToConsole("copy [programNameSource] [programNameDestination] - copies a program from source to destination");
                } else if (!isValidProgramName(commandParts[1]) || !isValidProgramName(commandParts[2])) {
                    playerCommandConsoleGui.appendToConsole("Invalid program name - only letters and digits allowed and a maximum length of 10");
                } else {
                    clientEntity.send(new CopyProgramEvent(computerId, commandParts[1], commandParts[2]));
                }
            } else if (commandParts[0].equals("rename")) {
                if (commandParts.length != 3) {
                    playerCommandConsoleGui.appendToConsole("Usage:");
                    playerCommandConsoleGui.appendToConsole("rename [programNameOld] [programNameNew] - renames a program from old to new name");
                } else if (!isValidProgramName(commandParts[1]) || !isValidProgramName(commandParts[2])) {
                    playerCommandConsoleGui.appendToConsole("Invalid program name - only letters and digits allowed and a maximum length of 10");
                } else {
                    clientEntity.send(new RenameProgramEvent(computerId, commandParts[1], commandParts[2]));
                }
            } else if (commandParts[0].equals("stop")) {
                if (commandParts.length > 1) {
                    playerCommandConsoleGui.appendToConsole("Usage:");
                    playerCommandConsoleGui.appendToConsole("stop - stops currently running program");
                } else {
                    clientEntity.send(new StopProgramEvent(computerId));
                }
            } else {
                if (commandParts[0].length() > 0) {
                    playerCommandConsoleGui.appendToConsole("Unknown command - " + commandParts[0]);
                }
            }
        }
    }

    private boolean isValidProgramName(String programName) {
        if (programName.length() > 10 || programName.length() == 0) {
            return false;
        }
        for (char c : programName.toCharArray()) {
            if (!Character.isDigit(c) && !Character.isLetter(c)) {
                return false;
            }
        }
        return true;
    }

    private void printHelp() {
        playerCommandConsoleGui.appendToConsole("help - prints this text");
        playerCommandConsoleGui.appendToConsole("copy [programNameSource] [programNameDestination] - copies a program from source to destination");
        playerCommandConsoleGui.appendToConsole("delete [programName] - deletes a program");
        playerCommandConsoleGui.appendToConsole("edit [programName] - edits a program in an editor");
        playerCommandConsoleGui.appendToConsole("execute [programName] - executes a program");
        playerCommandConsoleGui.appendToConsole("list - lists all programs on that computer");
        playerCommandConsoleGui.appendToConsole("rename [programNameOld] [programNameNew] - renames a program from old to new name");
        playerCommandConsoleGui.appendToConsole("stop - stops currently running program");
        playerCommandConsoleGui.appendToConsole("exit - exits this console");
    }

    public void clearComputerConsole() {
        computerConsole.clearConsole();
    }

    public void setComputerConsoleState(String[] lines) {
        computerConsole.setConsoleState(lines);
    }

    public void setComputerConsoleCharacters(int x, int y, String text) {
        computerConsole.setCharacters(x, y, text);
    }

    public void appendComputerConsoleLines(String[] lines) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < lines.length; i++) {
            if (i > 0) {
                result.append('\n');
            }
            result.append(lines[i]);
        }

        computerConsole.appendString(result.toString());
    }

    public void setProgramText(String programName, String programText) {
        playerCommandConsoleGui.setReadOnly(false);
        editingProgram = true;
        programEditingConsoleGui.setProgramText(programName, programText);
    }

    public enum TerminalMode {
        PLAYER_CONSOLE, COMPUTER_CONSOLE
    }
}

