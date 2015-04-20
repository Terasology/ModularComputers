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

import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.registry.CoreRegistry;
import org.terasology.rendering.nui.CoreScreenLayer;
import org.terasology.rendering.nui.NUIManager;
import org.terasology.rendering.nui.UIWidget;
import org.terasology.rendering.nui.widgets.ActivateEventListener;
import org.terasology.rendering.nui.widgets.UIButton;

public class ComputerTerminalWindow extends CoreScreenLayer {
    private ComputerTerminalWidget computerTerminalWidget;
    private UIButton playerConsole;
    private UIButton computerConsole;
    private UIButton documentation;

    @Override
    protected void initialise() {
        computerTerminalWidget = find("computerTerminal", ComputerTerminalWidget.class);
        playerConsole = find("playerConsole", UIButton.class);
        computerConsole = find("computerConsole", UIButton.class);
        documentation = find("documentation", UIButton.class);

        playerConsole.subscribe(
                new ActivateEventListener() {
                    @Override
                    public void onActivated(UIWidget widget) {
                        computerTerminalWidget.setMode(ComputerTerminalWidget.TerminalMode.PLAYER_CONSOLE);
                        requestFocusToTerminal();
                    }
                });
        computerConsole.subscribe(
                new ActivateEventListener() {
                    @Override
                    public void onActivated(UIWidget widget) {
                        computerTerminalWidget.setMode(ComputerTerminalWidget.TerminalMode.COMPUTER_CONSOLE);
                        requestFocusToTerminal();
                    }
                });
        documentation.subscribe(
                new ActivateEventListener() {
                    @Override
                    public void onActivated(UIWidget widget) {
                        computerTerminalWidget.setMode(ComputerTerminalWidget.TerminalMode.DOCUMENTATION);
                        requestFocusToTerminal();
                    }
                });
    }

    private void requestFocusToTerminal() {
        CoreRegistry.get(NUIManager.class).setFocus(computerTerminalWidget);
    }

    public void initializeWithEntities(EntityRef client, EntityRef computer) {
        computerTerminalWidget.setup(
                new Runnable() {
                    public void run() {
                        CoreRegistry.get(NUIManager.class).closeScreen(ComputerTerminalWindow.this);
                    }
                }, client, computer);
        requestFocusToTerminal();
    }

    public ComputerTerminalWidget getComputerTerminalWidget() {
        return computerTerminalWidget;
    }

    @Override
    public void onClosed() {
        computerTerminalWidget.onClosed();
    }

    @Override
    public boolean isModal() {
        return true;
    }
}
