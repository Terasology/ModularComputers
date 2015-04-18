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

import com.gempukku.lang.IllegalSyntaxException;
import org.lwjgl.input.Keyboard;
import org.terasology.computer.context.ComputerConsole;
import org.terasology.rendering.nui.Canvas;
import org.terasology.rendering.nui.Color;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProgramEditingConsoleGui {
    private static final int BLINK_LENGTH = 20;
    private static final Color PROGRAM_TEXT_COLOR = new Color(0xffffffff);
    private static final Color PROGRAM_CURSOR_COLOR = new Color(0x6666ffff);
    private static final Color PROGRAM_ERROR_UNDERLINE_COLOR = new Color(0xff0000ff);
    private static final Color PROGRAM_LAST_LINE_COLOR = new Color(0xff0000ff);
    private static final Color PROGRAM_ERROR_MESSAGE_COLOR = new Color(0xff0000ff);

    private static final Color COMPILE_PENDING_COLOR = new Color(0xffff00ff);
    private static final Color COMPILE_ERROR_COLOR = new Color(0xff0000ff);
    private static final Color COMPILE_OK_COLOR = new Color(0x00ff00ff);

    private boolean waitingForExitConfirmation = false;
    private boolean waitingForGotoLineEntered = false;
    private boolean displayErrorMessage = false;

    private boolean programSaveDirty;
    private boolean programCompileDirty;

    private String editedProgramName;
    private List<StringBuilder> editedProgramLines;

    private int editedProgramCursorX;
    private int editedProgramCursorY;
    private int editedDisplayStartX;
    private int editedDisplayStartY;

    private int blinkDrawTick;

    private ComputerTerminalWidget computerTerminalWidget;
    private CompileScriptOnTheFly onTheFlyCompiler;
    private StringBuilder gotoLineNumber;

    private int _scale = 0;
    private static final float[] SCALES = new float[]{1f, 0.833f, 0.667f, 0.5f};
    private static final int[] CHARACTER_COUNT_WIDTH = new int[]{ComputerConsole.CONSOLE_WIDTH, (int) (ComputerConsole.CONSOLE_WIDTH * 1.2f), (int) (ComputerConsole.CONSOLE_WIDTH * 1.5f), ComputerConsole.CONSOLE_WIDTH * 2};
    private static final int[] CHARACTER_COUNT_HEIGHT = new int[]{ComputerConsole.CONSOLE_HEIGHT, (int) (ComputerConsole.CONSOLE_HEIGHT * 1.2f), (int) (ComputerConsole.CONSOLE_HEIGHT * 1.5f), ComputerConsole.CONSOLE_HEIGHT * 2};

    public ProgramEditingConsoleGui(ComputerTerminalWidget ComputerConsoleWidget) {
        computerTerminalWidget = ComputerConsoleWidget;
        onTheFlyCompiler = new CompileScriptOnTheFly();
    }

    public void drawEditProgramConsole(Canvas canvas, int x, int y) {
        for (int line = editedDisplayStartY; line < Math.min(editedProgramLines.size(), editedDisplayStartY + getCharactersInColumn() - 1); line++) {
            String programLine = editedProgramLines.get(line).toString();
            if (programLine.length() > editedDisplayStartX) {
                String displayedLine = programLine.substring(editedDisplayStartX, Math.min(programLine.length(), editedDisplayStartX + getCharactersInRow()));
                computerTerminalWidget.drawMonospacedText(canvas, displayedLine, x, y + (line - editedDisplayStartY) * ComputerTerminalWidget.FONT_HEIGHT, PROGRAM_TEXT_COLOR);
            }
        }

        // Draw status line
        drawStatusLine(canvas, x, y);

        blinkDrawTick = ((++blinkDrawTick) % BLINK_LENGTH);
        if (blinkDrawTick * 2 > BLINK_LENGTH)
            computerTerminalWidget.drawVerticalLine(canvas, x + (editedProgramCursorX - editedDisplayStartX) * ComputerTerminalWidget.CHARACTER_WIDTH - 1, y + (editedProgramCursorY - editedDisplayStartY) * ComputerTerminalWidget.FONT_HEIGHT, y + 1 + (editedProgramCursorY - editedDisplayStartY + 1) * ComputerTerminalWidget.FONT_HEIGHT, PROGRAM_CURSOR_COLOR);
    }

    private int getCharactersInColumn() {
        return CHARACTER_COUNT_HEIGHT[_scale];
    }

    private int getCharactersInRow() {
        return CHARACTER_COUNT_WIDTH[_scale];
    }

    private void drawStatusLine(Canvas canvas, int x, int y) {
        final int lastLineY = y+ComputerTerminalWidget.FONT_HEIGHT * (getCharactersInColumn() - 1);
        final CompileScriptOnTheFly.CompileStatus compileStatusObj = onTheFlyCompiler.getCompileStatus();
        if (waitingForExitConfirmation) {
            computerTerminalWidget.drawMonospacedText(canvas, "File was not saved, exit? [Y]es/[N]o", 0, lastLineY, PROGRAM_LAST_LINE_COLOR);
        } else if (waitingForGotoLineEntered) {
            computerTerminalWidget.drawMonospacedText(canvas, "Go to line: " + gotoLineNumber.toString(), 0, lastLineY, PROGRAM_LAST_LINE_COLOR);
        } else if (displayErrorMessage && compileStatusObj != null && compileStatusObj.error != null) {
            displayErrorInformation(canvas, x, y, lastLineY, compileStatusObj);
        } else {
            displayNormalEditingInformation(canvas, x, y, lastLineY, compileStatusObj);
        }
    }

    private void displayNormalEditingInformation(Canvas canvas, int x, int y, int lastLineY, CompileScriptOnTheFly.CompileStatus compileStatusObj) {
        computerTerminalWidget.drawMonospacedText(canvas, "[S]ave E[x]it", x, lastLineY, PROGRAM_LAST_LINE_COLOR);

        if (programSaveDirty)
            computerTerminalWidget.drawMonospacedText(canvas, "*", x+15 * ComputerTerminalWidget.CHARACTER_WIDTH, lastLineY, PROGRAM_LAST_LINE_COLOR);

        String compileStatus = "...";
        Color compileColor = COMPILE_PENDING_COLOR;
        if (compileStatusObj != null) {
            if (compileStatusObj.success) {
                compileStatus = "OK";
                compileColor = COMPILE_OK_COLOR;
            } else if (compileStatusObj.error != null) {
                compileStatus = "[E]rror";
                compileColor = COMPILE_ERROR_COLOR;
            } else {
                compileStatus = "Unknown error";
                compileColor = COMPILE_ERROR_COLOR;
            }
        }

        int index = getCharactersInRow() - compileStatus.length();
        computerTerminalWidget.drawMonospacedText(canvas, compileStatus, x + index * ComputerTerminalWidget.CHARACTER_WIDTH, lastLineY, compileColor);

        if (compileStatusObj != null && compileStatusObj.error != null) {
            final IllegalSyntaxException error = compileStatusObj.error;
            final int errorLine = error.getLine() - editedDisplayStartY;
            final int errorColumn = error.getColumn() - editedDisplayStartX;

            if (errorLine >= 0 && errorLine < getCharactersInColumn() - 1
                    && errorColumn >= 0 && errorColumn < getCharactersInRow())
                computerTerminalWidget.drawHorizontalLine(canvas, x+errorColumn * ComputerTerminalWidget.CHARACTER_WIDTH, y+(errorLine + 1) * ComputerTerminalWidget.FONT_HEIGHT, x+(errorColumn + 1) * ComputerTerminalWidget.CHARACTER_WIDTH, PROGRAM_ERROR_UNDERLINE_COLOR);
        }
    }

    private void displayErrorInformation(Canvas canvas, int x, int y, int lastLineY, CompileScriptOnTheFly.CompileStatus compileStatusObj) {
        final IllegalSyntaxException error = compileStatusObj.error;
        computerTerminalWidget.drawMonospacedText(canvas, error.getError(), x, lastLineY, PROGRAM_ERROR_MESSAGE_COLOR);
        final int errorLine = error.getLine() - editedDisplayStartY;
        final int errorColumn = error.getColumn() - editedDisplayStartX;

        if (errorLine >= 0 && errorLine < getCharactersInColumn() - 1
                && errorColumn >= 0 && errorColumn < getCharactersInRow())
            computerTerminalWidget.drawHorizontalLine(canvas, x+errorColumn * ComputerTerminalWidget.CHARACTER_WIDTH, y+(errorLine + 1) * ComputerTerminalWidget.FONT_HEIGHT, x+(errorColumn + 1) * ComputerTerminalWidget.CHARACTER_WIDTH, PROGRAM_ERROR_UNDERLINE_COLOR);
    }

    public void keyTypedInEditingProgram(char character, int keyboardCharId, boolean controlDown) {
        if (waitingForExitConfirmation) {
            if (keyboardCharId == Keyboard.KEY_N)
                waitingForExitConfirmation = false;
            else if (keyboardCharId == Keyboard.KEY_Y) {
                waitingForExitConfirmation = false;
                computerTerminalWidget.exitProgramming();
            }
        } else if (waitingForGotoLineEntered) {
            if (character >= 32 && character < 127 && Character.isDigit(character) && gotoLineNumber.length() < 5) {
                gotoLineNumber.append(character);
            } else if (keyboardCharId == Keyboard.KEY_ESCAPE) {
                waitingForGotoLineEntered = false;
            } else if (keyboardCharId == Keyboard.KEY_BACK && gotoLineNumber.length() > 1) {
                gotoLineNumber.delete(gotoLineNumber.length() - 1, gotoLineNumber.length());
            } else if (keyboardCharId == Keyboard.KEY_RETURN) {
                if (gotoLineNumber.length() > 0) {
                    editedProgramCursorX = 0;
                    editedProgramCursorY = Math.min(Integer.parseInt(gotoLineNumber.toString()), editedProgramLines.size() - 1);
                }
                waitingForGotoLineEntered = false;
            }
        } else {
            StringBuilder editedLine = editedProgramLines.get(editedProgramCursorY);
            if (character >= 32 && character < 127) {
                editedLine.insert(editedProgramCursorX, character);
                editedProgramCursorX++;
                programModified();
            } else if (keyboardCharId == Keyboard.KEY_BACK) {
                handleBackspace(editedLine);
            } else if (keyboardCharId == Keyboard.KEY_DELETE) {
                handleDelete(editedLine);
            } else if (keyboardCharId == Keyboard.KEY_LEFT) {
                handleLeft();
            } else if (keyboardCharId == Keyboard.KEY_RIGHT) {
                handleRight(editedLine);
            } else if (keyboardCharId == Keyboard.KEY_UP && controlDown) {
                handleScaleUp();
            } else if (keyboardCharId == Keyboard.KEY_DOWN && controlDown) {
                handleScaleDown();
            } else if (keyboardCharId == Keyboard.KEY_UP && editedProgramCursorY > 0) {
                handleUp();
            } else if (keyboardCharId == Keyboard.KEY_DOWN && editedProgramCursorY < editedProgramLines.size() - 1) {
                handleDown();
            } else if (keyboardCharId == Keyboard.KEY_HOME) {
                handleHome();
            } else if (keyboardCharId == Keyboard.KEY_END) {
                handleEnd(editedLine);
            } else if (keyboardCharId == Keyboard.KEY_RETURN) {
                handleEnter(editedLine);
            } else if (keyboardCharId == Keyboard.KEY_S && controlDown) {
                handleSave();
            } else if (keyboardCharId == Keyboard.KEY_X && controlDown) {
                handleExit();
            } else if (keyboardCharId == Keyboard.KEY_E && controlDown) {
                handleDisplayError();
            } else if (keyboardCharId == Keyboard.KEY_G && controlDown) {
                handleGotoLine();
            } else if (keyboardCharId == Keyboard.KEY_V && controlDown) {
                handlePaste();
            }
        }

        // Adjust cursor X position to be within the program line
        if (editedProgramCursorX > editedProgramLines.get(editedProgramCursorY).length())
            editedProgramCursorX = editedProgramLines.get(editedProgramCursorY).length();

        final int editedLineLength = editedProgramLines.get(editedProgramCursorY).length();
        if (editedDisplayStartX + getCharactersInRow() > editedLineLength) {
            editedDisplayStartX = Math.max(0, editedLineLength - getCharactersInRow());
        } else if (editedProgramCursorX > editedDisplayStartX + getCharactersInRow()) {
            editedDisplayStartX = editedProgramCursorX - getCharactersInRow();
        } else if (editedProgramCursorX < editedDisplayStartX) {
            editedDisplayStartX = editedProgramCursorX;
        }

        final int linesCount = editedProgramLines.size();
        if (editedDisplayStartY + getCharactersInColumn() - 1 > linesCount) {
            editedDisplayStartY = Math.max(0, linesCount - getCharactersInColumn() + 1);
        } else if (editedProgramCursorY > editedDisplayStartY + getCharactersInColumn() - 2) {
            editedDisplayStartY = editedProgramCursorY - getCharactersInColumn() + 2;
        } else if (editedProgramCursorY < editedDisplayStartY) {
            editedDisplayStartY = editedProgramCursorY;
        }

        if (programCompileDirty) {
            onTheFlyCompiler.submitCompileRequest(getProgramText());
            programCompileDirty = false;
        }
    }

    private void handleScaleDown() {
        if (_scale > 0)
            _scale--;
    }

    private void handleScaleUp() {
        if (_scale < SCALES.length - 1)
            _scale++;
    }


    private String getClipboardContents() {
        Transferable t = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);

        try {
            if (t != null && t.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                return (String) t.getTransferData(DataFlavor.stringFlavor);
            }
        } catch (UnsupportedFlavorException | IOException e) {
            // TODO
        }

        return "";
    }


    private void handlePaste() {
        final String clipboard = getClipboardContents();
        final String[] lines = clipboard.split("\n");
        for (int index = 0; index < lines.length; index++) {
            String line = lines[index];
            final String fixedLine = ComputerConsole.stripInvalidCharacters(line);
            final StringBuilder currentLine = editedProgramLines.get(editedProgramCursorY);
            String before = currentLine.substring(0, editedProgramCursorX);
            String after = currentLine.substring(editedProgramCursorX);
            if (index < lines.length - 1) {
                editedProgramLines.set(editedProgramCursorY, new StringBuilder(before + fixedLine));
                editedProgramLines.add(editedProgramCursorY + 1, new StringBuilder(after));
                editedProgramCursorY++;
                editedProgramCursorX = 0;
            } else {
                // Last line
                editedProgramLines.set(editedProgramCursorY, new StringBuilder(before + fixedLine + after));
                editedProgramCursorX = (before + fixedLine).length();
            }
        }
        if (clipboard.length() > 0)
            programModified();
    }

    private void handleDisplayError() {
        final CompileScriptOnTheFly.CompileStatus compileStatus = onTheFlyCompiler.getCompileStatus();
        if (compileStatus != null && compileStatus.error != null) {
            final IllegalSyntaxException error = compileStatus.error;
            editedProgramCursorY = error.getLine();
            editedProgramCursorX = error.getColumn();
            displayErrorMessage = true;
        }
    }

    private void handleExit() {
        if (!programSaveDirty) {
            onTheFlyCompiler.finishedEditing();
            computerTerminalWidget.exitProgramming();
        } else {
            waitingForExitConfirmation = true;
        }
    }

    private void handleSave() {
        String program = getProgramText();
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream os = new DataOutputStream(baos);
            os.writeUTF(editedProgramName);
            os.writeUTF(program);
            computerTerminalWidget.requestSave(editedProgramName, program);
            programSaveDirty = false;
        } catch (IOException exp) {
            // TODO
        }
    }

    private void handleGotoLine() {
        gotoLineNumber = new StringBuilder();
        waitingForGotoLineEntered = true;
    }

    private void handleEnter(StringBuilder editedLine) {
        String remainingInLine = editedLine.substring(editedProgramCursorX);
        editedLine.delete(editedProgramCursorX, editedLine.length());
        int spaceCount = getSpaceCount(editedLine.toString());
        char[] spacesPrefix = new char[spaceCount];
        Arrays.fill(spacesPrefix, ' ');
        editedProgramLines.add(editedProgramCursorY + 1, new StringBuilder(new String(spacesPrefix) + remainingInLine));
        editedProgramCursorX = spaceCount;
        editedProgramCursorY++;
        programModified();
    }

    private int getSpaceCount(String s) {
        final char[] chars = s.toCharArray();
        for (int i = 0; i < chars.length; i++)
            if (chars[i] != ' ')
                return i;
        return chars.length;
    }

    private void handleEnd(StringBuilder editedLine) {
        editedProgramCursorX = editedLine.length();
    }

    private void handleHome() {
        editedProgramCursorX = 0;
    }

    private void handleDown() {
        editedProgramCursorY++;
    }

    private void handleUp() {
        editedProgramCursorY--;
    }

    private void handleRight(StringBuilder editedLine) {
        if (editedProgramCursorX < editedLine.length()) {
            editedProgramCursorX++;
        } else if (editedProgramCursorY < editedProgramLines.size() - 1) {
            editedProgramCursorX = 0;
            editedProgramCursorY++;
        }
    }

    private void handleLeft() {
        if (editedProgramCursorX > 0) {
            editedProgramCursorX--;
        } else if (editedProgramCursorY > 0) {
            editedProgramCursorX = editedProgramLines.get(editedProgramCursorY - 1).length();
            editedProgramCursorY--;
        }
    }

    private void handleDelete(StringBuilder editedLine) {
        if (editedProgramCursorX < editedLine.length()) {
            editedLine.delete(editedProgramCursorX, editedProgramCursorX + 1);
        } else if (editedProgramCursorY < editedProgramLines.size() - 1) {
            editedLine.append(editedProgramLines.get(editedProgramCursorY + 1));
            editedProgramLines.remove(editedProgramCursorY + 1);
        }
        programModified();
    }

    private void handleBackspace(StringBuilder editedLine) {
        if (editedProgramCursorX > 0) {
            editedLine.delete(editedProgramCursorX - 1, editedProgramCursorX);
            editedProgramCursorX--;
        } else if (editedProgramCursorY > 0) {
            StringBuilder previousLine = editedProgramLines.get(editedProgramCursorY - 1);
            editedProgramCursorX = previousLine.length();
            previousLine.append(editedLine);
            editedProgramLines.remove(editedProgramCursorY);
            editedProgramCursorY--;
        }
        programModified();
    }

    private String getProgramText() {
        StringBuilder program = new StringBuilder();
        for (int i = 0; i < editedProgramLines.size(); i++) {
            if (i > 0)
                program.append("\n");
            program.append(editedProgramLines.get(i));
        }
        return program.toString();
    }

    public void setProgramText(String programName, String programText) {
        editedProgramName = programName;
        editedProgramLines = new ArrayList<StringBuilder>();
        for (String line : programText.split("\n"))
            editedProgramLines.add(new StringBuilder(line));

        editedProgramCursorX = 0;
        editedProgramCursorY = 0;
        editedDisplayStartX = 0;
        editedDisplayStartY = 0;
        programSaveDirty = false;
        programCompileDirty = true;

        onTheFlyCompiler.submitCompileRequest(getProgramText());
        onTheFlyCompiler.startCompiler();
    }

    private void programModified() {
        programSaveDirty = true;
        programCompileDirty = true;
        displayErrorMessage = false;
    }
}
