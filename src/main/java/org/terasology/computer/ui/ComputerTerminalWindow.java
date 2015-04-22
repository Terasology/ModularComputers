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

import com.gempukku.lang.ObjectDefinition;
import org.terasology.asset.Assets;
import org.terasology.browser.data.BrowserPageInfo;
import org.terasology.browser.data.ParagraphData;
import org.terasology.browser.data.TableOfContents;
import org.terasology.browser.data.basic.DefaultBrowserData;
import org.terasology.browser.data.basic.HyperlinkParagraphData;
import org.terasology.browser.data.basic.PageData;
import org.terasology.browser.ui.BrowserHyperlinkListener;
import org.terasology.browser.ui.BrowserWidget;
import org.terasology.browser.ui.style.TextRenderStyle;
import org.terasology.computer.system.common.ComputerLanguageContext;
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
import org.terasology.rendering.nui.widgets.ItemSelectEventListener;
import org.terasology.rendering.nui.widgets.UIButton;
import org.terasology.rendering.nui.widgets.UIList;

import java.util.Collection;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ComputerTerminalWindow extends CoreScreenLayer {
    private ComputerTerminalWidget computerTerminalWidget;

    private Deque<String> browserHistory = new LinkedList<>();
    private Deque<String> browserFuture = new LinkedList<>();
    private UIButton backButton;
    private UIButton forwardButton;

    private DefaultBrowserData browserData;
    private BrowserWidget browser;

    @Override
    protected void initialise() {
        computerTerminalWidget = find("computerTerminal", ComputerTerminalWidget.class);

        UIButton playerConsole = find("playerConsole", UIButton.class);
        UIButton computerConsole = find("computerConsole", UIButton.class);
        UIButton documentation = find("documentation", UIButton.class);
        final CardLayout tabs = find("tabs", CardLayout.class);
        browser = find("browser", BrowserWidget.class);
        browser.addBrowserHyperlinkListener(
                new BrowserHyperlinkListener() {
                    @Override
                    public void hyperlinkClicked(String hyperlink) {
                        if (hyperlink.startsWith("navigate:")) {
                            navigateTo(hyperlink.substring(9));
                        }
                    }
                });

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
                        navigateTo("introduction");
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
    }

    private HyperlinkParagraphData createTitleParagraph(String title) {
        HyperlinkParagraphData paragraphData = new HyperlinkParagraphData(null);
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

    private DefaultBrowserData buildDocumentation(ComputerLanguageContextInitializer computerLanguageContextInitializer) {
        DefaultBrowserData defaultBrowserData = new DefaultBrowserData();

        defaultBrowserData.addEntry(null, buildIntroductionPage());
        buildBuiltinObjectsPages(defaultBrowserData, computerLanguageContextInitializer);

        return defaultBrowserData;
    }

    private void buildBuiltinObjectsPages(DefaultBrowserData defaultBrowserData, ComputerLanguageContextInitializer computerLanguageContextInitializer) {
        computerLanguageContextInitializer.initializeContext(
                new ComputerLanguageContext() {
                    @Override
                    public void addObject(String object, ObjectDefinition objectDefinition, Collection<ParagraphData> objectDescription, Map<String, Collection<ParagraphData>> functionDescriptions, Map<String, Map<String, Collection<ParagraphData>>> functionParametersDescriptions, Map<String, Collection<ParagraphData>> functionReturnDescriptions) {
                        String objectPageId = "built-in-" + object;

                        PageData pageData = new PageData(objectPageId, "Variable - " + object, null);
                        pageData.addParagraph(createTitleParagraph("Variable - " + object));
                        for (ParagraphData paragraphData : objectDescription) {
                            pageData.addParagraph(paragraphData);
                        }
                        pageData.addParagraph(simpleParagraphData("Functions:"));
                        for (String functionName : functionDescriptions.keySet()) {
                            HyperlinkParagraphData paragraphData = new HyperlinkParagraphData(null);
                            paragraphData.append(functionName, new TextRenderStyle() {
                                @Override
                                public Font getFont() {
                                    return null;
                                }

                                @Override
                                public Color getColor() {
                                    return Color.BLUE;
                                }
                            }, "navigate:" + object + "-" + functionName);
                            pageData.addParagraph(paragraphData);
                        }

                        defaultBrowserData.addEntry(null, pageData);

                        for (Map.Entry<String, Collection<ParagraphData>> functionEntry : functionDescriptions.entrySet()) {
                            String functionName = functionEntry.getKey();

                            PageData functionPageData = new PageData(object + "-" + functionName, "Function - " + functionName, null);
                            functionPageData.addParagraph(createTitleParagraph("Function - " + functionName));
                            for (ParagraphData paragraphData : functionEntry.getValue()) {
                                functionPageData.addParagraph(paragraphData);
                            }

                            functionPageData.addParagraph(simpleParagraphData("Parameters:"));

                            Map<String, Collection<ParagraphData>> functionParameters = functionParametersDescriptions.get(functionName);

                            if (functionParameters.isEmpty()) {
                                functionPageData.addParagraph(simpleParagraphData("None"));
                            }
                            for (Map.Entry<String, Collection<ParagraphData>> parameterDescription : functionParameters.entrySet()) {
                                functionPageData.addParagraph(simpleParagraphData(parameterDescription.getKey()));
                                for (ParagraphData paragraphData : parameterDescription.getValue()) {
                                    functionPageData.addParagraph(paragraphData);
                                }
                            }

                            Collection<ParagraphData> returnDescription = functionReturnDescriptions.get(functionName);
                            if (returnDescription != null) {
                                functionPageData.addParagraph(simpleParagraphData("Returns:"));
                                for (ParagraphData paragraphData : returnDescription) {
                                    functionPageData.addParagraph(paragraphData);
                                }
                            }

                            defaultBrowserData.addEntry(objectPageId, functionPageData);
                        }
                    }
                });
    }

    private HyperlinkParagraphData simpleParagraphData(String text) {
        HyperlinkParagraphData hyperlinkParagraphData = new HyperlinkParagraphData(null);
        hyperlinkParagraphData.append(text, null, null);
        return hyperlinkParagraphData;
    }

    private PageData buildIntroductionPage() {
        PageData pageData = new PageData("introduction", "Introduction", null);
        pageData.addParagraph(createTitleParagraph("Introduction"));
        pageData.addParagraph(simpleParagraphData("This is the first page of the documentation."));
        return pageData;
    }

    private void setupTableOfContents(TableOfContents tableOfContents) {
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

        tocList.subscribeSelection(
                new ItemSelectEventListener<BrowserPageInfo>() {
                    @Override
                    public void onItemSelected(UIWidget widget, BrowserPageInfo item) {
                        navigateTo(item.getPageId());
                    }
                });
    }

    private void populateChildrenOfParent(TableOfContents tableOfContents, List<BrowserPageInfo> items, int level, String parent) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < level; i++) {
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
                    return prefix + content.getDisplayableTitle();
                }
            });
            populateChildrenOfParent(tableOfContents, items, level + 1, content.getPageId());
        }
    }

    private void navigateTo(String page) {
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

    public void initializeTerminal(ComputerLanguageContextInitializer computerLanguageContextInitializer,
                                   EntityRef client, EntityRef computer) {
        if (browserData == null) {
            browserData = buildDocumentation(computerLanguageContextInitializer);

            setupTableOfContents(browserData);

            browser.setBrowserData(browserData);
            navigateTo("introduction");
        }

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
