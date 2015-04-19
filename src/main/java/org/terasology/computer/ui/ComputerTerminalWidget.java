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
package org.terasology.computer.ui;

import org.codehaus.plexus.util.StringUtils;
import org.terasology.computer.context.ComputerConsole;
import org.terasology.computer.event.server.ConsoleListeningRegistrationEvent;
import org.terasology.computer.event.server.DeleteProgramEvent;
import org.terasology.computer.event.server.ExecuteProgramEvent;
import org.terasology.computer.event.server.GetProgramTextEvent;
import org.terasology.computer.event.server.ListProgramsEvent;
import org.terasology.computer.event.server.SaveProgramEvent;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.input.Keyboard;
import org.terasology.input.MouseInput;
import org.terasology.input.events.KeyEvent;
import org.terasology.math.Rect2i;
import org.terasology.math.Vector2i;
import org.terasology.rendering.assets.font.Font;
import org.terasology.rendering.nui.Canvas;
import org.terasology.rendering.nui.Color;
import org.terasology.rendering.nui.CoreWidget;
import org.terasology.rendering.nui.FocusManager;
import org.terasology.rendering.nui.HorizontalAlign;
import org.terasology.rendering.nui.InteractionListener;
import org.terasology.rendering.nui.VerticalAlign;

import java.util.Collection;

public class ComputerTerminalWidget extends CoreWidget {
    public static final Color BACKGROUND_COLOR = new Color(0x111111ff);
    public static final Color FRAME_COLOR = new Color(0xffffffff);
    public static final Color BUTTON_TEXT_COLOR = new Color(0xffffffff);
    public static final Color BUTTON_BG_HOVER_COLOR = new Color(0x7f7f7fff);
    public static final Color BUTTON_BG_ACTIVE_COLOR = new Color(0xbfbfbfff);
    public static final Color BUTTON_BG_INACTIVE_COLOR = new Color(0x3f3f3fff);

    public static final Color COMPUTER_CONSOLE_TEXT_COLOR = new Color(0xffffffff);

    private static final int PADDING_HOR = 5;
    private static final int PADDING_VER = 5;
    private static final int BUTTON_PADDING_HOR = 3;
    private static final int BUTTON_PADDING_VER = 3;

    public static final int FONT_HEIGHT = 16;
    public static final int CHARACTER_WIDTH = 14;
    public static final String PLAYER_CONSOLE_TEXT = "Player console";
    public static final String COMPUTER_CONSOLE_TEXT = "Computer console";

    // mode=0 is user (player) console, mode=1 is program output console
    private int mode = 0;
    private boolean editingProgram;

    private ComputerConsole computerConsole;

    private Runnable closeRunnable;
    private EntityRef clientEntity;
    private EntityRef computerEntity;

    private Rect2i playerConsoleModeButton;
    private Rect2i computerConsoleModeButton;

    private PlayerCommandConsoleGui playerCommandConsoleGui;
    private ProgramEditingConsoleGui programEditingConsoleGui;

    private Vector2i mousePosition = new Vector2i(-1, -1);

    public void setup(Runnable closeRunnable, EntityRef clientEntity, EntityRef computerEntity) {
        mode = 0;
        editingProgram = false;
        computerConsole = new ComputerConsole();

        this.closeRunnable = closeRunnable;
        this.clientEntity = clientEntity;
        this.computerEntity = computerEntity;

        playerCommandConsoleGui = new PlayerCommandConsoleGui(this);
        playerCommandConsoleGui.appendToConsole("AutomationOS v. 0.0");
        String userName = "player";
        playerCommandConsoleGui.appendToConsole("You're logged in as " + userName + ", use \"exit\" command to exit the console, use \"help\" to list commands.");
        programEditingConsoleGui = new ProgramEditingConsoleGui(this);

        this.clientEntity.send(new ConsoleListeningRegistrationEvent(this.computerEntity, true));
    }

    public void onClosed() {
        clientEntity.send(new ConsoleListeningRegistrationEvent(computerEntity, false));
    }

    public void appendToPlayerConsole(String text) {
        playerCommandConsoleGui.appendToConsole(text);
    }

    public void displayProgramList(Collection<String> programs) {
        if (!programs.isEmpty()) {
            String programList = StringUtils.join(programs.iterator(), " ");
            playerCommandConsoleGui.appendToConsole(programList);
        }
        playerCommandConsoleGui.setReadOnly(false);
    }

    @Override
    public void onDraw(Canvas canvas) {
        Rect2i region = canvas.getRegion();

        int screenWidth = region.width();
        int screenHeight = region.height();

        // Fill background with solid dark-grey
        canvas.drawFilledRectangle(Rect2i.createFromMinAndSize(0, 0, screenWidth, screenHeight), BACKGROUND_COLOR);

        // Draw white rectangle around the screen
        drawHorizontalLine(canvas, 0, 0, screenWidth, FRAME_COLOR);
        drawHorizontalLine(canvas, 0, screenHeight, screenWidth, FRAME_COLOR);
        drawVerticalLine(canvas, 0, 0, screenHeight, FRAME_COLOR);
        drawVerticalLine(canvas, screenWidth, 0, screenHeight, FRAME_COLOR);

        int buttonHeight = BUTTON_PADDING_VER * 2 + FONT_HEIGHT;

        int playerConsoleButtonWidth = getFont(canvas).getWidth(PLAYER_CONSOLE_TEXT) + BUTTON_PADDING_HOR * 2;
        int computerConsoleButtonWidth = getFont(canvas).getWidth(COMPUTER_CONSOLE_TEXT) + BUTTON_PADDING_HOR * 2;

        int mouseX = mousePosition.x;
        int mouseY = mousePosition.y;

        boolean playerConsoleHover = mouseX >= PADDING_HOR && mouseX < PADDING_HOR + playerConsoleButtonWidth
                && mouseY >= PADDING_VER && mouseY < PADDING_VER + buttonHeight;
        boolean computerConsoleHover = mouseX >= PADDING_HOR + playerConsoleButtonWidth && mouseX < PADDING_HOR + playerConsoleButtonWidth + computerConsoleButtonWidth
                && mouseY >= PADDING_VER && mouseY < PADDING_VER + buttonHeight;

        Color playerConsoleButBgColor = getButBgColor(playerConsoleHover, mode == 0);
        Color computerConsoleButBgColor = getButBgColor(computerConsoleHover, mode == 1);

        // Draw button backgrounds
        canvas.drawFilledRectangle(Rect2i.createFromMinAndMax(PADDING_HOR, PADDING_VER, PADDING_HOR + playerConsoleButtonWidth, PADDING_VER + buttonHeight), playerConsoleButBgColor);
        canvas.drawFilledRectangle(Rect2i.createFromMinAndMax(PADDING_HOR + playerConsoleButtonWidth, PADDING_VER, PADDING_HOR + playerConsoleButtonWidth + computerConsoleButtonWidth, PADDING_VER + buttonHeight), computerConsoleButBgColor);

        playerConsoleModeButton = Rect2i.createFromMinAndSize(PADDING_HOR, PADDING_VER, playerConsoleButtonWidth, buttonHeight);
        computerConsoleModeButton = Rect2i.createFromMinAndSize(PADDING_HOR + playerConsoleButtonWidth, PADDING_VER, computerConsoleButtonWidth, buttonHeight);

        // Draw button texts
        canvas.drawTextRaw(PLAYER_CONSOLE_TEXT, getFont(canvas), BUTTON_TEXT_COLOR, playerConsoleModeButton);
        canvas.drawTextRaw(COMPUTER_CONSOLE_TEXT, getFont(canvas), BUTTON_TEXT_COLOR, computerConsoleModeButton);

        if (mode == 0)
            drawPlayerConsole(canvas);
        else if (mode == 1)
            drawComputerConsole(canvas);

        canvas.addInteractionRegion(
                new InteractionListener() {
                    @Override
                    public void setFocusManager(FocusManager focusManager) {

                    }

                    @Override
                    public void onMouseOver(Vector2i pos, boolean topMostElement) {
                        mousePosition.set(pos);
                    }

                    @Override
                    public void onMouseLeave() {
                        mousePosition.set(-1, -1);
                    }

                    @Override
                    public boolean onMouseClick(MouseInput button, Vector2i pos) {
                        return mouseClicked(pos.x, pos.y, button.getId());
                    }

                    @Override
                    public boolean onMouseDoubleClick(MouseInput button, Vector2i pos) {
                        return false;
                    }

                    @Override
                    public void onMouseDrag(Vector2i pos) {

                    }

                    @Override
                    public boolean onMouseWheel(int wheelTurns, Vector2i pos) {
                        return false;
                    }

                    @Override
                    public void onMouseRelease(MouseInput button, Vector2i pos) {

                    }

                    @Override
                    public boolean isMouseOver() {
                        return false;
                    }
                }
        );
    }

    private Font getFont(Canvas canvas) {
        return canvas.getSkin().getDefaultStyle().getFont();
    }

    @Override
    public Vector2i getPreferredContentSize(Canvas canvas, Vector2i sizeHint) {
        int width = PADDING_HOR * 2 + CHARACTER_WIDTH * ComputerConsole.CONSOLE_WIDTH;
        int height = PADDING_VER * 2 + (BUTTON_PADDING_VER * 2 + FONT_HEIGHT) + FONT_HEIGHT * ComputerConsole.CONSOLE_HEIGHT;
        return new Vector2i(width, height);
    }

    protected void requestSave(String programName, String programText) {
        clientEntity.send(new SaveProgramEvent(computerEntity, programName, programText));
    }

    private void drawPlayerConsole(Canvas canvas) {
        if (editingProgram) {
            programEditingConsoleGui.drawEditProgramConsole(canvas, PADDING_HOR, PADDING_VER + BUTTON_PADDING_VER * 2 + FONT_HEIGHT);
        } else {
            playerCommandConsoleGui.drawPlayerCommandConsole(canvas, PADDING_HOR, PADDING_VER + BUTTON_PADDING_VER * 2 + FONT_HEIGHT);
        }
    }

    private void drawComputerConsole(Canvas canvas) {
        final String[] consoleLines = computerConsole.getLines();
        for (int i = 0; i < consoleLines.length; i++)
            drawMonospacedText(canvas, consoleLines[i], PADDING_HOR, PADDING_VER + BUTTON_PADDING_VER * 2 + FONT_HEIGHT + i * FONT_HEIGHT, COMPUTER_CONSOLE_TEXT_COLOR);
    }

    public void drawVerticalLine(Canvas canvas, int x, int y1, int y2, Color color) {
        canvas.drawLine(x, y1, x, y2, color);
    }

    public void drawHorizontalLine(Canvas canvas, int x1, int y, int x2, Color color) {
        canvas.drawLine(x1, y, x2, y, color);
    }

    protected void drawMonospacedText(Canvas canvas, String text, int x, int y, Color color) {
        // For some reason the text is actually drawn a bit higher than expected, so to correct it, I add 2 to "y"
        char[] chars = text.toCharArray();
        for (int i = 0; i < chars.length; i++)
            renderCharAt(canvas, chars[i], x + i * CHARACTER_WIDTH, y, color);
    }

    private void renderCharAt(Canvas canvas, char ch, int x, int y, Color color) {
        canvas.drawTextRaw(String.valueOf(ch), getFont(canvas), color, Rect2i.createFromMinAndSize(x, y, CHARACTER_WIDTH, FONT_HEIGHT), HorizontalAlign.CENTER, VerticalAlign.TOP);
    }

    private boolean mouseClicked(int mouseX, int mouseY, int which) {
        if (mode == 0 && computerConsoleModeButton.contains(mouseX, mouseY)) {
            mode = 1;
            return true;
        } else if (mode == 1 && playerConsoleModeButton.contains(mouseX, mouseY)) {
            mode = 0;
            return true;
        }
        return false;
    }

    @Override
    public void onKeyEvent(KeyEvent event) {
        if (isFocused()) {
            int keyboardCharId = event.getKey().getId();
            if (event.isDown()) {
                char character = event.getKeyCharacter();
                if (mode == 0) {
                    if (editingProgram) {
                        programEditingConsoleGui.keyTypedInEditingProgram(character, keyboardCharId,
                                Keyboard.isKeyDown(Keyboard.KeyId.LEFT_CTRL) || Keyboard.isKeyDown(Keyboard.KeyId.RIGHT_CTRL));
                    } else {
                        playerCommandConsoleGui.keyTypedInPlayerConsole(character, keyboardCharId);
                    }
                }
            }
            if (keyboardCharId != Keyboard.KeyId.ESCAPE)
                event.consume();
        }
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
                    clientEntity.send(new GetProgramTextEvent(computerEntity, programName));
                }
            } else if (commandParts[0].equals("execute")) {
                if (commandParts.length != 2) {
                    playerCommandConsoleGui.appendToConsole("Usage:");
                    playerCommandConsoleGui.appendToConsole("execute [programName] - executes specified program");
                } else if (!isValidProgramName(commandParts[1]))
                    playerCommandConsoleGui.appendToConsole("Invalid program name - only letters and digits allowed and a maximum length of 10");
                else {
                    clientEntity.send(new ExecuteProgramEvent(computerEntity, commandParts[1]));
                }
            } else if (commandParts[0].equals("list")) {
                if (commandParts.length > 1) {
                    playerCommandConsoleGui.appendToConsole("Usage:");
                    playerCommandConsoleGui.appendToConsole("list - lists all programs on that computer");
                } else {
                    playerCommandConsoleGui.appendToConsole("Retrieving list of programs...");

                    playerCommandConsoleGui.setReadOnly(true);
                    clientEntity.send(new ListProgramsEvent(computerEntity));
                }
            } else if (commandParts[0].equals("delete")) {
                if (commandParts.length != 2) {
                    playerCommandConsoleGui.appendToConsole("Usage:");
                    playerCommandConsoleGui.appendToConsole("delete [programName] - deletes specified program");
                } else if (!isValidProgramName(commandParts[1]))
                    playerCommandConsoleGui.appendToConsole("Invalid program name - only letters and digits allowed and a maximum length of 10");
                else {
                    clientEntity.send(new DeleteProgramEvent(computerEntity, commandParts[1]));
                }
            } else {
                if (commandParts[0].length() > 0)
                    playerCommandConsoleGui.appendToConsole("Unknown command - " + commandParts[0]);
            }
        }
    }

    private boolean isValidProgramName(String programName) {
        if (programName.length() > 10 || programName.length() == 0)
            return false;
        for (char c : programName.toCharArray()) {
            if (!Character.isDigit(c) && !Character.isLetter(c))
                return false;
        }
        return true;
    }

    private void printHelp() {
        playerCommandConsoleGui.appendToConsole("help - prints this text");
        playerCommandConsoleGui.appendToConsole("edit [programName] - edits a program in an editor");
        playerCommandConsoleGui.appendToConsole("execute [programName] - executes a program");
        playerCommandConsoleGui.appendToConsole("list - lists all programs on that computer");
        playerCommandConsoleGui.appendToConsole("delete [programName] - deletes a program");
        playerCommandConsoleGui.appendToConsole("exit - exits this console");
    }

    private Color getButBgColor(boolean hover, boolean inMode) {
        if (hover)
            return BUTTON_BG_HOVER_COLOR;
        else if (inMode)
            return BUTTON_BG_ACTIVE_COLOR;
        else
            return BUTTON_BG_INACTIVE_COLOR;
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
            if (i > 0)
                result.append('\n');
            result.append(lines[i]);
        }

        computerConsole.appendString(result.toString());
    }

    public void setProgramText(String programName, String programText) {
        playerCommandConsoleGui.setReadOnly(false);
        editingProgram = true;
        programEditingConsoleGui.setProgramText(programName, programText);
    }

    @Override
    public boolean canBeFocus() {
        return true;
    }
}

