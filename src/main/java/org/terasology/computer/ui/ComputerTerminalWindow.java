// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.computer.ui;

import org.terasology.computer.system.common.ComputerLanguageContextInitializer;
import org.terasology.computer.ui.documentation.DefaultDocumentationData;
import org.terasology.computer.ui.documentation.DocumentationBuilder;
import org.terasology.computer.ui.documentation.DocumentationPageInfo;
import org.terasology.computer.ui.documentation.TableOfContents;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.logic.clipboard.ClipboardManager;
import org.terasology.engine.registry.CoreRegistry;
import org.terasology.engine.rendering.nui.CoreScreenLayer;
import org.terasology.engine.rendering.nui.NUIManager;
import org.terasology.engine.rendering.nui.widgets.browser.data.basic.HTMLLikeParser;
import org.terasology.engine.rendering.nui.widgets.browser.ui.BrowserHyperlinkListener;
import org.terasology.engine.rendering.nui.widgets.browser.ui.BrowserWidget;
import org.terasology.nui.UIWidget;
import org.terasology.nui.itemRendering.StringTextRenderer;
import org.terasology.nui.layouts.CardLayout;
import org.terasology.nui.widgets.ActivateEventListener;
import org.terasology.nui.widgets.ItemActivateEventListener;
import org.terasology.nui.widgets.ItemSelectEventListener;
import org.terasology.nui.widgets.UIButton;
import org.terasology.nui.widgets.UIList;

import java.util.Collection;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class ComputerTerminalWindow extends CoreScreenLayer {
    private ComputerTerminalWidget computerTerminalWidget;

    private Deque<String> browserHistory = new LinkedList<>();
    private Deque<String> browserFuture = new LinkedList<>();
    private UIButton backButton;
    private UIButton forwardButton;

    private DefaultDocumentationData documentationData;
    private BrowserWidget browser;
    private CardLayout tabs;

    private UIButton playerConsoleTabButton;
    private UIButton computerConsoleTabButton;
    private UIButton documentationTabButton;

    private Set<String> expandedPageIds = new HashSet<>();
    private UIList<DocumentationPageInfo> tocList;

    @Override
    public void initialise() {
        computerTerminalWidget = find("computerTerminal", ComputerTerminalWidget.class);

        playerConsoleTabButton = find("playerConsole", UIButton.class);
        computerConsoleTabButton = find("computerConsole", UIButton.class);
        documentationTabButton = find("documentation", UIButton.class);
        tocList = find("tableOfContents", UIList.class);

        tabs = find("tabs", CardLayout.class);
        browser = find("browser", BrowserWidget.class);
        browser.addBrowserHyperlinkListener(
                new BrowserHyperlinkListener() {
                    @Override
                    public void hyperlinkClicked(String hyperlink) {
                        if (hyperlink.startsWith("navigate:")) {
                            navigateTo(hyperlink.substring(9), true);
                        } else if (hyperlink.startsWith("saveAs:")) {
                            String[] split = hyperlink.substring(7).split(":", 2);
                            saveAs(HTMLLikeParser.unencodeHTMLLike(split[0]), HTMLLikeParser.unencodeHTMLLike(split[1]));
                        }
                    }
                });

        playerConsoleTabButton.subscribe(
                new ActivateEventListener() {
                    @Override
                    public void onActivated(UIWidget widget) {
                        setTabButtonsState(false, true, true);
                        tabs.setDisplayedCard("computerTerminal");
                        computerTerminalWidget.setMode(ComputerTerminalWidget.TerminalMode.PLAYER_CONSOLE);
                        requestFocusToTerminal();
                    }
                });
        computerConsoleTabButton.subscribe(
                new ActivateEventListener() {
                    @Override
                    public void onActivated(UIWidget widget) {
                        setTabButtonsState(true, false, true);
                        tabs.setDisplayedCard("computerTerminal");
                        computerTerminalWidget.setMode(ComputerTerminalWidget.TerminalMode.COMPUTER_CONSOLE);
                        requestFocusToTerminal();
                    }
                });
        documentationTabButton.subscribe(
                new ActivateEventListener() {
                    @Override
                    public void onActivated(UIWidget widget) {
                        setTabButtonsState(true, true, false);
                        tabs.setDisplayedCard("browserTab");
                    }
                });

        playerConsoleTabButton.setEnabled(false);

        UIButton homeButton = find("homeButton", UIButton.class);
        backButton = find("backButton", UIButton.class);
        forwardButton = find("forwardButton", UIButton.class);

        homeButton.subscribe(
                new ActivateEventListener() {
                    @Override
                    public void onActivated(UIWidget widget) {
                        navigateTo("introduction", true);
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
                        navigateTo(previousPage, false);
                    }
                });
        forwardButton.subscribe(
                new ActivateEventListener() {
                    @Override
                    public void onActivated(UIWidget widget) {
                        String nextPage = browserFuture.removeFirst();
                        browserHistory.add(nextPage);
                        navigateTo(nextPage, false);
                    }
                });
    }

    @Override
    public void onGainFocus() {
        if (computerTerminalWidget != null) {
            requestFocusToTerminal();
        }
    }

    private void setTabButtonsState(boolean playerConsole, boolean computerConsole, boolean documentation) {
        playerConsoleTabButton.setEnabled(playerConsole);
        computerConsoleTabButton.setEnabled(computerConsole);
        documentationTabButton.setEnabled(documentation);
    }

    private void setupTableOfContents() {
        resetTableOfContentsDisplay();

        tocList.setItemRenderer(new StringTextRenderer<DocumentationPageInfo>(false) {
            @Override
            public String getString(DocumentationPageInfo value) {
                int level = 0;
                DocumentationPageInfo lookingAt = value;
                while (lookingAt.getParentPageId() != null) {
                    lookingAt = documentationData.getPageInfo(lookingAt.getParentPageId());
                    level++;
                }
                StringBuilder prefix = new StringBuilder();
                for (int i = 0; i < level; i++) {
                    prefix.append("    ");
                }

                if (documentationData.getContents(value.getPageId()).isEmpty()) {
                    return prefix + value.getDisplayableTitle();
                } else {
                    return prefix + "+ " + value.getDisplayableTitle();
                }
            }

            @Override
            public String getTooltip(DocumentationPageInfo value) {
                return value.getDisplayableTitle().trim();
            }
        });

        tocList.subscribeSelection(
                new ItemSelectEventListener<DocumentationPageInfo>() {
                    @Override
                    public void onItemSelected(UIWidget widget, DocumentationPageInfo item) {
                        navigateTo(item.getPageId(), true);
                    }
                });
        tocList.subscribe(
                new ItemActivateEventListener<DocumentationPageInfo>() {
                    @Override
                    public void onItemActivated(UIWidget widget, DocumentationPageInfo item) {
                        String pageId = item.getPageId();
                        if (expandedPageIds.contains(pageId)) {
                            expandedPageIds.remove(pageId);
                        } else {
                            expandedPageIds.add(pageId);
                        }
                        resetTableOfContentsDisplay();
                    }
                }
        );
    }

    private void resetTableOfContentsDisplay() {
        List<DocumentationPageInfo> items = new LinkedList<>();
        int level = 0;
        String parent = null;
        populateChildrenOfParent(documentationData, items, level, parent);
        tocList.setList(items);
    }

    private void populateChildrenOfParent(TableOfContents tableOfContents, List<DocumentationPageInfo> items, int level, String parent) {
        if (parent == null || expandedPageIds.contains(parent)) {
            Collection<DocumentationPageInfo> contents = tableOfContents.getContents(parent);
            for (DocumentationPageInfo content : contents) {
                items.add(content);
                populateChildrenOfParent(tableOfContents, items, level + 1, content.getPageId());
            }
        }
    }

    private void navigateTo(String page, boolean modifyHistory) {
        if (browserHistory.peekLast() == null || !browserHistory.peekLast().equals(page) || !modifyHistory) {
            if (modifyHistory) {
                browserHistory.add(page);
                browserFuture.clear();
            }
            updateHistoryButtons();
            browser.navigateTo(documentationData.getDocument(page));

            DocumentationPageInfo pageInfo = documentationData.getPageInfo(page);

            DocumentationPageInfo parent = documentationData.getPageInfo(pageInfo.getParentPageId());
            while (parent != null) {
                expandedPageIds.add(parent.getPageId());
                parent = documentationData.getPageInfo(parent.getParentPageId());
            }
            resetTableOfContentsDisplay();

            tocList.setSelection(pageInfo);
        }
    }

    private void saveAs(String programName, String code) {
        computerTerminalWidget.saveProgram(programName, code);
    }

    private void updateHistoryButtons() {
        backButton.setEnabled(browserHistory.size() > 1);
        forwardButton.setEnabled(browserFuture.size() > 0);
    }

    private void requestFocusToTerminal() {
        CoreRegistry.get(NUIManager.class).setFocus(computerTerminalWidget);
    }

    public void initializeTerminal(ComputerLanguageContextInitializer computerLanguageContextInitializer,
                                   ClipboardManager clipboardManager,
                                   EntityRef client, int computerId) {
        if (documentationData == null) {
            documentationData = DocumentationBuilder.buildDocumentation(computerLanguageContextInitializer);

            setupTableOfContents();

            navigateTo("introduction", true);
        }

        computerTerminalWidget.setup(
                computerLanguageContextInitializer, clipboardManager,
                new Runnable() {
                    public void run() {
                        CoreRegistry.get(NUIManager.class).closeScreen(ComputerTerminalWindow.this);
                    }
                }, client, computerId);
        requestFocusToTerminal();
    }

    public ComputerTerminalWidget getComputerTerminalWidget() {
        return computerTerminalWidget;
    }

    @Override
    public void onClosed() {
        computerTerminalWidget.onClosed();
    }
}
