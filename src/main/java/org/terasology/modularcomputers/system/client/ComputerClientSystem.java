// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.modularcomputers.system.client;

import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.event.ReceiveEvent;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterMode;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.logic.clipboard.ClipboardManager;
import org.terasology.engine.logic.common.ActivateEvent;
import org.terasology.engine.registry.In;
import org.terasology.engine.rendering.nui.NUIManager;
import org.terasology.modularcomputers.component.ComputerComponent;
import org.terasology.modularcomputers.component.ComputerTerminalComponent;
import org.terasology.modularcomputers.event.client.ForceTerminalCloseEvent;
import org.terasology.modularcomputers.event.client.ProgramExecutionResultEvent;
import org.terasology.modularcomputers.event.client.ProgramListReceivedEvent;
import org.terasology.modularcomputers.event.client.ProgramTextReceivedEvent;
import org.terasology.modularcomputers.event.client.console.AppendConsoleLinesEvent;
import org.terasology.modularcomputers.event.client.console.ClearConsoleScreenEvent;
import org.terasology.modularcomputers.event.client.console.SetConsoleCharactersAtEvent;
import org.terasology.modularcomputers.event.client.console.SetConsoleScreenEvent;
import org.terasology.modularcomputers.system.common.ComputerLanguageContextInitializer;
import org.terasology.modularcomputers.ui.ComputerTerminalWidget;
import org.terasology.modularcomputers.ui.ComputerTerminalWindow;

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
            window.initializeTerminal(computerLanguageContextInitializer, clipboardManager, client,
                    computerComponent.computerId);

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
