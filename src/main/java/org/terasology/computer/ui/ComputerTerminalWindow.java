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

import org.terasology.asset.Assets;
import org.terasology.browser.data.BrowserPageInfo;
import org.terasology.browser.data.ParagraphData;
import org.terasology.browser.data.TableOfContents;
import org.terasology.browser.data.basic.DefaultBrowserData;
import org.terasology.browser.data.basic.HyperlinkParagraphData;
import org.terasology.browser.data.basic.PageData;
import org.terasology.browser.ui.BrowserWidget;
import org.terasology.browser.ui.style.TextRenderStyle;
import org.terasology.computer.system.common.ComputerLanguageContextInitializer;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.registry.CoreRegistry;
import org.terasology.rendering.assets.font.Font;
import org.terasology.rendering.nui.Color;
import org.terasology.rendering.nui.CoreScreenLayer;
import org.terasology.rendering.nui.NUIManager;
import org.terasology.rendering.nui.UIWidget;
import org.terasology.rendering.nui.itemRendering.StringTextRenderer;
import org.terasology.rendering.nui.layouts.CardLayout;
import org.terasology.rendering.nui.widgets.ActivateEventListener;
import org.terasology.rendering.nui.widgets.ItemActivateEventListener;
import org.terasology.rendering.nui.widgets.UIButton;
import org.terasology.rendering.nui.widgets.UIList;

import java.util.Collection;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

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

        DefaultBrowserData defaultBrowserData = buildDocumentation();

        setupTableOfContents(browser, defaultBrowserData);

        browser.setBrowserData(defaultBrowserData);
        navigateTo(browser, "introduction");
    }

    private HyperlinkParagraphData createTitleParagraph(String title) {
        HyperlinkParagraphData paragraphData = new HyperlinkParagraphData();
        paragraphData.append(title, new TextRenderStyle() {
            @Override
            public Font getFont() {
                return Assets.getFont("engine:title");
            }

            @Override
            public Color getColor() {
                return null;
            }
        }, null);
        return paragraphData;
    }

    private DefaultBrowserData buildDocumentation() {
        DefaultBrowserData defaultBrowserData = new DefaultBrowserData();
        PageData pageData = new PageData("introduction", "Introduction", null);
        pageData.addHyperlinkableParagraph(null, createTitleParagraph("Introduction"));
        HyperlinkParagraphData paragraphData = new HyperlinkParagraphData();
        paragraphData.append("This is the first page of the documentation.", null, null);
        pageData.addHyperlinkableParagraph(null, paragraphData);
        defaultBrowserData.addEntry(null, pageData);
        return defaultBrowserData;
    }

    private void setupTableOfContents(BrowserWidget browser, TableOfContents tableOfContents) {
        UIList<BrowserPageInfo> tocList = find("tableOfContents", UIList.class);
        List<BrowserPageInfo> items = new LinkedList<>();
        int level = 0;
        String parent = null;
        populateChildrenOfParent(tableOfContents, items, level, parent);

        tocList.setItemRenderer(new StringTextRenderer<BrowserPageInfo>() {
            @Override
            public String getString(BrowserPageInfo value) {
                return value.getDisplayableTitle();
            }
        });
        tocList.setList(items);

        tocList.subscribe(
                new ItemActivateEventListener<BrowserPageInfo>() {
                    @Override
                    public void onItemActivated(UIWidget widget, BrowserPageInfo item) {
                        navigateTo(browser, item.getPageId());
                    }
                }
        );
    }

    private void populateChildrenOfParent(TableOfContents tableOfContents, List<BrowserPageInfo> items, int level, String parent) {
        StringBuilder sb = new StringBuilder();
        for (int i=0; i<level; i++) {
            sb.append("  ");
        }
        String prefix = sb.toString();
        Collection<BrowserPageInfo> contents = tableOfContents.getContents(parent);
        for (BrowserPageInfo content : contents) {
            items.add(new BrowserPageInfo() {
                @Override
                public String getPageId() {
                    return content.getPageId();
                }

                @Override
                public String getDisplayableTitle() {
                    return prefix+content.getDisplayableTitle();
                }
            });
            populateChildrenOfParent(tableOfContents, items, level+1, content.getPageId());
        }
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

    public void initializeWithEntities(ComputerLanguageContextInitializer computerLanguageContextInitializer,
                                       EntityRef client, EntityRef computer) {
        computerTerminalWidget.setup(
                computerLanguageContextInitializer,
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
//
//    @Override
//    public boolean isModal() {
//        return true;
//    }
}
