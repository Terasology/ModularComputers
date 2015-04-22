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

import org.terasology.browser.data.basic.DefaultBrowserData;
import org.terasology.browser.data.basic.HyperlinkParagraphData;
import org.terasology.browser.data.basic.PageData;
import org.terasology.browser.ui.BrowserWidget;
import org.terasology.browser.ui.style.TextRenderStyle;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.registry.CoreRegistry;
import org.terasology.rendering.assets.font.Font;
import org.terasology.rendering.nui.Color;
import org.terasology.rendering.nui.CoreScreenLayer;
import org.terasology.rendering.nui.NUIManager;
import org.terasology.rendering.nui.UIWidget;
import org.terasology.rendering.nui.layouts.CardLayout;
import org.terasology.rendering.nui.widgets.ActivateEventListener;
import org.terasology.rendering.nui.widgets.UIButton;

import java.util.Deque;
import java.util.LinkedList;

public class ComputerTerminalWindow extends CoreScreenLayer {
    private ComputerTerminalWidget computerTerminalWidget;

    private Deque<String> browserHistory = new LinkedList<>();
    private Deque<String> browserFuture = new LinkedList<>();
    private UIButton backButton;
    private UIButton forwardButton;

    @Override
    protected void initialise() {
        computerTerminalWidget = find("computerTerminal", ComputerTerminalWidget.class);

        UIButton playerConsole = find("playerConsole", UIButton.class);
        UIButton computerConsole = find("computerConsole", UIButton.class);
        UIButton documentation = find("documentation", UIButton.class);
        final CardLayout tabs = find("tabs", CardLayout.class);
        BrowserWidget browser = find("browser", BrowserWidget.class);

        playerConsole.subscribe(
                new ActivateEventListener() {
                    @Override
                    public void onActivated(UIWidget widget) {
                        tabs.setDisplayedCard("computerTerminal");
                        computerTerminalWidget.setMode(ComputerTerminalWidget.TerminalMode.PLAYER_CONSOLE);
                        requestFocusToTerminal();
                    }
                });
        computerConsole.subscribe(
                new ActivateEventListener() {
                    @Override
                    public void onActivated(UIWidget widget) {
                        tabs.setDisplayedCard("computerTerminal");
                        computerTerminalWidget.setMode(ComputerTerminalWidget.TerminalMode.COMPUTER_CONSOLE);
                        requestFocusToTerminal();
                    }
                });
        documentation.subscribe(
                new ActivateEventListener() {
                    @Override
                    public void onActivated(UIWidget widget) {
                        tabs.setDisplayedCard("browserTab");
                        requestFocusToTerminal();
                    }
                });

        UIButton homeButton = find("homeButton", UIButton.class);
        backButton = find("backButton", UIButton.class);
        forwardButton = find("forwardButton", UIButton.class);
        homeButton.subscribe(
                new ActivateEventListener() {
                    @Override
                    public void onActivated(UIWidget widget) {
                        navigateTo(browser, "introduction");
                    }
                }
        );
        backButton.subscribe(
                new ActivateEventListener() {
                    @Override
                    public void onActivated(UIWidget widget) {
                        String currentPage = browserHistory.removeLast();
                        browserFuture.addFirst(currentPage);
                        String previousPage = browserHistory.peekLast();
                        browser.navigateTo(previousPage);
                        updateHistoryButtons();
                    }
                });
        forwardButton.subscribe(
                new ActivateEventListener() {
                    @Override
                    public void onActivated(UIWidget widget) {
                        String nextPage = browserFuture.removeFirst();
                        browserHistory.add(nextPage);
                        browser.navigateTo(nextPage);
                        updateHistoryButtons();
                    }
                });

        DefaultBrowserData defaultBrowserData = new DefaultBrowserData();
        PageData pageData = new PageData("introduction", "Introduction", null);
        HyperlinkParagraphData paragraphData = new HyperlinkParagraphData();
        paragraphData.append("This is a very long text that will hopefully wrap around in the browser window." +
                " It was not enough, so I added this sentence. ", null, null);
        paragraphData.append("Hello again!", new TextRenderStyle() {
            @Override
            public Font getFont() {
                return null;
            }

            @Override
            public Color getColor() {
                return new Color(1f, 0f, 0f);
            }
        }, null);
        pageData.addHyperlinkableParagraph(null, paragraphData);
        defaultBrowserData.addEntry(null, pageData);

        browser.setBrowserData(defaultBrowserData);
        navigateTo(browser, "introduction");
    }

    private void navigateTo(BrowserWidget browser, String page) {
        if (browserHistory.peekLast() == null || !browserHistory.peekLast().equals(page)) {
            browserHistory.add(page);
            browser.navigateTo(page);
            browserFuture.clear();
            updateHistoryButtons();
        }
    }

    private void updateHistoryButtons() {
        backButton.setEnabled(browserHistory.size() > 1);
        forwardButton.setEnabled(browserFuture.size() > 0);
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
