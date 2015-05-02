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
package org.terasology.computer.system.client;

import org.terasology.computer.component.ComputerComponent;
import org.terasology.computer.component.ComputerTerminalComponent;
import org.terasology.computer.event.client.ForceTerminalCloseEvent;
import org.terasology.computer.event.client.ProgramExecutionResultEvent;
import org.terasology.computer.event.client.ProgramListReceivedEvent;
import org.terasology.computer.event.client.ProgramTextReceivedEvent;
import org.terasology.computer.event.client.console.AppendConsoleLinesEvent;
import org.terasology.computer.event.client.console.ClearConsoleScreenEvent;
import org.terasology.computer.event.client.console.SetConsoleCharactersAtEvent;
import org.terasology.computer.event.client.console.SetConsoleScreenEvent;
import org.terasology.computer.system.common.ComputerLanguageContextInitializer;
import org.terasology.computer.ui.ComputerTerminalWidget;
import org.terasology.computer.ui.ComputerTerminalWindow;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.logic.clipboard.ClipboardManager;
import org.terasology.logic.common.ActivateEvent;
import org.terasology.registry.In;
import org.terasology.rendering.nui.NUIManager;

@RegisterSystem(RegisterMode.CLIENT)
public class ComputerClientSystem extends BaseComponentSystem {
    private static final String COMPUTER_TERMINAL_UI = "ModularComputers:ComputerTerminal";

    @In
    private NUIManager nuiManager;
    @In
    private ComputerLanguageContextInitializer computerLanguageContextInitializer;
    @In
    private ClipboardManager clipboardManager;

    @ReceiveEvent
    public void terminalActivated(ActivateEvent event, EntityRef item, ComputerTerminalComponent component) {
        EntityRef target = event.getTarget();
        if (target.hasComponent(ComputerComponent.class)) {
            ComputerComponent computerComponent = target.getComponent(ComputerComponent.class);

            EntityRef client = event.getInstigator();

            nuiManager.pushScreen(COMPUTER_TERMINAL_UI);

            ComputerTerminalWindow window = (ComputerTerminalWindow) nuiManager.getScreen(COMPUTER_TERMINAL_UI);
            window.initializeTerminal(computerLanguageContextInitializer, clipboardManager, client, computerComponent.computerId);

            event.consume();
        }
    }

    @ReceiveEvent
    public void programExecutionResult(ProgramExecutionResultEvent event, EntityRef client) {
        ComputerTerminalWindow window = (ComputerTerminalWindow) nuiManager.getScreen(COMPUTER_TERMINAL_UI);
        if (window != null) {
            ComputerTerminalWidget computerTerminalWidget = window.getComputerTerminalWidget();
            if (computerTerminalWidget.getComputerId() == event.getComputerId()) {
                computerTerminalWidget.appendToPlayerConsole(event.getMessage());
            }
        }
    }

    @ReceiveEvent
    public void appendToConsole(AppendConsoleLinesEvent event, EntityRef client) {
        ComputerTerminalWindow window = (ComputerTerminalWindow) nuiManager.getScreen(COMPUTER_TERMINAL_UI);
        if (window != null) {
            ComputerTerminalWidget computerTerminalWidget = window.getComputerTerminalWidget();
            if (computerTerminalWidget.getComputerId() == event.getComputerId()) {
                computerTerminalWidget.appendComputerConsoleLines(event.getLines());
            }
        }
    }

    @ReceiveEvent
    public void clearConsole(ClearConsoleScreenEvent event, EntityRef client) {
        ComputerTerminalWindow window = (ComputerTerminalWindow) nuiManager.getScreen(COMPUTER_TERMINAL_UI);
        if (window != null) {
            ComputerTerminalWidget computerTerminalWidget = window.getComputerTerminalWidget();
            if (computerTerminalWidget.getComputerId() == event.getComputerId()) {
                computerTerminalWidget.clearComputerConsole();
            }
        }
    }

    @ReceiveEvent
    public void setConsoleCharacters(SetConsoleCharactersAtEvent event, EntityRef client) {
        ComputerTerminalWindow window = (ComputerTerminalWindow) nuiManager.getScreen(COMPUTER_TERMINAL_UI);
        if (window != null) {
            ComputerTerminalWidget computerTerminalWidget = window.getComputerTerminalWidget();
            if (computerTerminalWidget.getComputerId() == event.getComputerId()) {
                computerTerminalWidget.setComputerConsoleCharacters(event.getX(), event.getY(), event.getText());
            }
        }
    }

    @ReceiveEvent
    public void setConsoleScreen(SetConsoleScreenEvent event, EntityRef client) {
        ComputerTerminalWindow window = (ComputerTerminalWindow) nuiManager.getScreen(COMPUTER_TERMINAL_UI);
        if (window != null) {
            ComputerTerminalWidget computerTerminalWidget = window.getComputerTerminalWidget();
            if (computerTerminalWidget.getComputerId() == event.getComputerId()) {
                computerTerminalWidget.setComputerConsoleState(event.getScreenLines());
            }
        }
    }

    @ReceiveEvent
    public void programTextReceived(ProgramTextReceivedEvent event, EntityRef client) {
        ComputerTerminalWindow window = (ComputerTerminalWindow) nuiManager.getScreen(COMPUTER_TERMINAL_UI);
        if (window != null) {
            ComputerTerminalWidget computerTerminalWidget = window.getComputerTerminalWidget();
            if (computerTerminalWidget.getComputerId() == event.getComputerId()) {
                computerTerminalWidget.setProgramText(event.getProgramName(), event.getProgramText());
            }
        }
    }

    @ReceiveEvent
    public void programsListReceived(ProgramListReceivedEvent event, EntityRef client) {
        ComputerTerminalWindow window = (ComputerTerminalWindow) nuiManager.getScreen(COMPUTER_TERMINAL_UI);
        if (window != null) {
            ComputerTerminalWidget computerTerminalWidget = window.getComputerTerminalWidget();
            if (computerTerminalWidget.getComputerId() == event.getComputerId()) {
                computerTerminalWidget.displayProgramList(event.getPrograms());
            }
        }
    }

    @ReceiveEvent
    public void forceTerminalCloseReceived(ForceTerminalCloseEvent event, EntityRef client) {
        ComputerTerminalWindow window = (ComputerTerminalWindow) nuiManager.getScreen(COMPUTER_TERMINAL_UI);
        if (window != null) {
            nuiManager.closeScreen(window);
        }
    }
}
