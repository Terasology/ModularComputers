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
package org.terasology.computer.ui.documentation;

import com.gempukku.lang.ObjectDefinition;
import org.terasology.asset.Assets;
import org.terasology.browser.data.ParagraphData;
import org.terasology.browser.data.basic.HTMLLikeParser;
import org.terasology.browser.data.basic.HyperlinkParagraphData;
import org.terasology.browser.ui.style.ParagraphRenderStyle;
import org.terasology.browser.ui.style.TextRenderStyle;
import org.terasology.computer.system.common.ComputerLanguageContext;
import org.terasology.computer.system.common.ComputerLanguageContextInitializer;
import org.terasology.computer.system.server.lang.ComputerModule;
import org.terasology.rendering.assets.font.Font;
import org.terasology.rendering.nui.Color;

import java.util.Collection;
import java.util.Map;

public class DocumentationBuilder {
    private DocumentationBuilder() {
    }

    public static String getComputerModulePageId(String moduleType) {
        return "computer-module-"+moduleType;
    }

    public static String getComputerModuleMethodPageId(String moduleType, String methodName) {
        return moduleType+"-"+methodName;
    }

    public static String getBuiltInObjectPageId(String object) {
        return "built-in-"+object;
    }

    public static String getBuiltInObjectMethodPageId(String object, String methodName) {
        return object+"-"+methodName;
    }

    public static DefaultDocumentationData buildDocumentation(ComputerLanguageContextInitializer computerLanguageContextInitializer) {
        DefaultDocumentationData defaultBrowserData = new DefaultDocumentationData();

        defaultBrowserData.addEntry(null, buildIntroductionPage(computerLanguageContextInitializer));
        buildBuiltinObjectPages(defaultBrowserData, computerLanguageContextInitializer);
        buildComputerModulePages(defaultBrowserData, computerLanguageContextInitializer);

        return defaultBrowserData;
    }


    private static void buildComputerModulePages(DefaultDocumentationData defaultBrowserData, ComputerLanguageContextInitializer computerLanguageContextInitializer) {
        computerLanguageContextInitializer.initializeContext(
                new ComputerLanguageContext() {
                    @Override
                    public void addComputerModule(ComputerModule computerModule, String description, Map<String, String> methodDescriptions,
                                                  Map<String, Map<String, String>> methodParametersDescriptions, Map<String, String> methodReturnDescriptions) {
                        String moduleType = computerModule.getModuleType();
                        String modulePageId = getComputerModulePageId(moduleType);

                        PageData pageData = new PageData(modulePageId, "Computer Module - " + computerModule.getModuleName(), null);
                        pageData.addParagraph(createTitleParagraph("Computer module - " + computerModule.getModuleName()));
                        pageData.addParagraphs(HTMLLikeParser.parseHTMLLike(null, description));
                        pageData.addParagraph(paragraphWithSpaceBefore("Methods:"));
                        for (String methodName : methodDescriptions.keySet()) {
                            HyperlinkParagraphData paragraphData = new HyperlinkParagraphData(null);
                            paragraphData.append(methodName, null, "navigate:" + getComputerModuleMethodPageId(moduleType, methodName));
                            pageData.addParagraph(paragraphData);
                        }

                        defaultBrowserData.addEntry(null, pageData);

                        for (Map.Entry<String, String> methodEntry : methodDescriptions.entrySet()) {
                            String methodName = methodEntry.getKey();

                            PageData functionPageData = new PageData(getComputerModuleMethodPageId(moduleType, methodName), "Method - " + methodName, null);
                            functionPageData.addParagraph(createTitleParagraph("Method - " + methodName));
                            functionPageData.addParagraphs(HTMLLikeParser.parseHTMLLike(null, methodEntry.getValue()));

                            functionPageData.addParagraph(paragraphWithSpaceBefore("Parameters:"));

                            Map<String, String> methodParameters = methodParametersDescriptions.get(methodName);

                            if (methodParameters == null || methodParameters.isEmpty()) {
                                functionPageData.addParagraphs(HTMLLikeParser.parseHTMLLike(null, "None"));
                            } else {
                                for (Map.Entry<String, String> parameterDescription : methodParameters.entrySet()) {
                                    functionPageData.addParagraphs(HTMLLikeParser.parseHTMLLike(null, parameterDescription.getKey() + " - " + parameterDescription.getValue()));
                                }
                            }

                            Collection<ParagraphData> returnDescription = HTMLLikeParser.parseHTMLLike(null, methodReturnDescriptions.get(methodName));
                            if (!returnDescription.isEmpty()) {
                                functionPageData.addParagraph(paragraphWithSpaceBefore("Returns:"));
                                functionPageData.addParagraphs(returnDescription);
                            }

                            defaultBrowserData.addEntry(modulePageId, functionPageData);
                        }
                    }

                    @Override
                    public void addObject(String object, ObjectDefinition objectDefinition, String objectDescription, Map<String, String> functionDescriptions, Map<String, Map<String, String>> functionParametersDescriptions, Map<String, String> functionReturnDescriptions) {
                        // Ignore
                    }
                });
    }

    private static void buildBuiltinObjectPages(DefaultDocumentationData defaultBrowserData, ComputerLanguageContextInitializer computerLanguageContextInitializer) {
        computerLanguageContextInitializer.initializeContext(
                new ComputerLanguageContext() {
                    @Override
                    public void addObject(String object, ObjectDefinition objectDefinition, String objectDescription, Map<String, String> functionDescriptions, Map<String, Map<String, String>> functionParametersDescriptions, Map<String, String> functionReturnDescriptions) {
                        String objectPageId = getBuiltInObjectPageId(object);

                        PageData pageData = new PageData(objectPageId, "Variable - " + object, null);
                        pageData.addParagraph(createTitleParagraph("Variable - " + object));
                        pageData.addParagraphs(HTMLLikeParser.parseHTMLLike(null, objectDescription));
                        pageData.addParagraph(paragraphWithSpaceBefore("Functions:"));
                        for (String functionName : functionDescriptions.keySet()) {
                            HyperlinkParagraphData paragraphData = new HyperlinkParagraphData(null);
                            paragraphData.append(functionName, null, "navigate:" + getBuiltInObjectMethodPageId(object, functionName));
                            pageData.addParagraph(paragraphData);
                        }

                        defaultBrowserData.addEntry(null, pageData);

                        for (Map.Entry<String, String> functionEntry : functionDescriptions.entrySet()) {
                            String functionName = functionEntry.getKey();

                            PageData functionPageData = new PageData(getBuiltInObjectMethodPageId(object, functionName), "Function - " + functionName, null);
                            functionPageData.addParagraph(createTitleParagraph("Function - " + functionName));
                            functionPageData.addParagraphs(HTMLLikeParser.parseHTMLLike(null, functionEntry.getValue()));

                            functionPageData.addParagraph(paragraphWithSpaceBefore("Parameters:"));

                            Map<String, String> functionParameters = functionParametersDescriptions.get(functionName);

                            if (functionParameters.isEmpty()) {
                                functionPageData.addParagraphs(HTMLLikeParser.parseHTMLLike(null, "None"));
                            }
                            for (Map.Entry<String, String> parameterDescription : functionParameters.entrySet()) {
                                functionPageData.addParagraphs(HTMLLikeParser.parseHTMLLike(null, parameterDescription.getKey() + " - " + parameterDescription.getValue()));
                            }

                            Collection<ParagraphData> returnDescription = HTMLLikeParser.parseHTMLLike(null, functionReturnDescriptions.get(functionName));
                            if (!returnDescription.isEmpty()) {
                                functionPageData.addParagraph(paragraphWithSpaceBefore("Returns:"));
                                functionPageData.addParagraphs(returnDescription);
                            }

                            defaultBrowserData.addEntry(objectPageId, functionPageData);
                        }
                    }

                    @Override
                    public void addComputerModule(ComputerModule computerModule, String description, Map<String, String> methodDescriptions, Map<String, Map<String, String>> methodParametersDescriptions, Map<String, String> methodReturnDescriptions) {
                        // Ignore
                    }
                });
    }

    private static PageData buildIntroductionPage(ComputerLanguageContextInitializer computerLanguageContextInitializer) {
        PageData pageData = new PageData("introduction", "Introduction", null);
        pageData.addParagraph(createTitleParagraph("Introduction"));
        pageData.addParagraphs(HTMLLikeParser.parseHTMLLike(null, "This is the first page of the documentation."));

        pageData.addParagraph(paragraphWithSpaceBefore("List of built-in objects:"));

        computerLanguageContextInitializer.initializeContext(
                new ComputerLanguageContext() {
                    @Override
                    public void addObject(String object, ObjectDefinition objectDefinition, String objectDescription, Map<String, String> functionDescriptions, Map<String, Map<String, String>> functionParametersDescriptions, Map<String, String> functionReturnDescriptions) {
                        HyperlinkParagraphData paragraphData = new HyperlinkParagraphData(null);
                        paragraphData.append(object, null, "navigate:"+getBuiltInObjectPageId(object));
                        pageData.addParagraph(paragraphData);
                    }

                    @Override
                    public void addComputerModule(ComputerModule computerModule, String description, Map<String, String> methodDescriptions, Map<String, Map<String, String>> methodParametersDescriptions, Map<String, String> methodReturnDescriptions) {
                        // Ignore
                    }
                });

        pageData.addParagraph(paragraphWithSpaceBefore("List of registered computer modules:"));

        computerLanguageContextInitializer.initializeContext(
                new ComputerLanguageContext() {
                    @Override
                    public void addObject(String object, ObjectDefinition objectDefinition, String objectDescription, Map<String, String> functionDescriptions, Map<String, Map<String, String>> functionParametersDescriptions, Map<String, String> functionReturnDescriptions) {
                        // Ignore
                    }

                    @Override
                    public void addComputerModule(ComputerModule computerModule, String description, Map<String, String> methodDescriptions, Map<String, Map<String, String>> methodParametersDescriptions, Map<String, String> methodReturnDescriptions) {
                        HyperlinkParagraphData paragraphData = new HyperlinkParagraphData(null);
                        paragraphData.append(computerModule.getModuleName(), null, "navigate:"+getComputerModulePageId(computerModule.getModuleType()));
                        pageData.addParagraph(paragraphData);
                    }
                });

        return pageData;
    }

    private static HyperlinkParagraphData paragraphWithSpaceBefore(String text) {
        HyperlinkParagraphData hyperlinkParagraphData = new HyperlinkParagraphData(
                new ParagraphRenderStyle() {
                    @Override
                    public Integer getIndentAbove(boolean firstParagraph) {
                        return 10;
                    }

                    @Override
                    public Integer getIndentBelow(boolean lastParagraph) {
                        return null;
                    }

                    @Override
                    public Integer getIndentLeft() {
                        return null;
                    }

                    @Override
                    public Integer getIndentRight() {
                        return null;
                    }

                    @Override
                    public Font getFont(boolean hyperlink) {
                        return null;
                    }

                    @Override
                    public Color getColor(boolean hyperlink) {
                        return null;
                    }
                }
        );
        hyperlinkParagraphData.append(text, null, null);
        return hyperlinkParagraphData;
    }

    private static HyperlinkParagraphData createTitleParagraph(String title) {
        HyperlinkParagraphData paragraphData = new HyperlinkParagraphData(null);
        paragraphData.append(title, new TextRenderStyle() {
            @Override
            public Font getFont(boolean hyperlink) {
                return Assets.getFont("engine:title");
            }

            @Override
            public Color getColor(boolean hyperlink) {
                return null;
            }
        }, null);
        return paragraphData;
    }
}
