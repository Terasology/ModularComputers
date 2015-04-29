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
import org.terasology.browser.data.ParagraphData;
import org.terasology.browser.data.basic.HTMLLikeParser;
import org.terasology.browser.ui.style.ParagraphRenderStyle;
import org.terasology.computer.system.common.ComputerLanguageContext;
import org.terasology.computer.system.common.ComputerLanguageContextInitializer;
import org.terasology.computer.system.server.lang.ComputerModule;
import org.terasology.rendering.nui.Color;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class DocumentationBuilder {
    private DocumentationBuilder() {
    }

    public static String getComputerModulePageId(String moduleType) {
        return "computer-module-" + moduleType;
    }

    public static String getComputerModuleMethodPageId(String moduleType, String methodName) {
        return moduleType + "-" + methodName;
    }

    public static String getBuiltInObjectPageId(String object) {
        return "built-in-" + object;
    }

    public static String getBuiltInObjectMethodPageId(String object, String methodName) {
        return object + "-" + methodName;
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
                    public void addComputerModule(ComputerModule computerModule, String description, Collection<ParagraphData> additionalParagraphs, Map<String, String> methodDescriptions,
                                                  Map<String, Map<String, String>> methodParametersDescriptions, Map<String, String> methodReturnDescriptions, Map<String, Collection<ParagraphData>> methodAdditionalParagraphs) {
                        String moduleType = computerModule.getModuleType();
                        String modulePageId = getComputerModulePageId(moduleType);

                        PageData pageData = new PageData(modulePageId, "Module - " + computerModule.getModuleName(), null);
                        pageData.addParagraphs(createTitleParagraph("Module - " + computerModule.getModuleName()));
                        pageData.addParagraphs(HTMLLikeParser.parseHTMLLike(null, description));
                        pageData.addParagraphs(paragraphWithSpaceBefore("Methods:"));
                        for (String methodName : methodDescriptions.keySet()) {
                            pageData.addParagraphs(
                                    HTMLLikeParser.parseHTMLLike(null,
                                            " * <h navigate:" + getComputerModuleMethodPageId(moduleType, methodName) + ">" + methodName + "()</h> - " + methodDescriptions.get(methodName)));
                        }

                        pageData.addParagraphs(additionalParagraphs);

                        defaultBrowserData.addEntry(null, pageData);

                        for (Map.Entry<String, String> methodEntry : methodDescriptions.entrySet()) {
                            String methodName = methodEntry.getKey();

                            PageData functionPageData = new PageData(getComputerModuleMethodPageId(moduleType, methodName), methodName + "()", null);
                            functionPageData.addParagraphs(createTitleParagraph("Method - " + methodName));
                            functionPageData.addParagraphs(HTMLLikeParser.parseHTMLLike(null, methodEntry.getValue()));

                            functionPageData.addParagraphs(paragraphWithSpaceBefore("Parameters:"));

                            Map<String, String> methodParameters = methodParametersDescriptions.get(methodName);

                            if (methodParameters == null || methodParameters.isEmpty()) {
                                functionPageData.addParagraphs(HTMLLikeParser.parseHTMLLike(null, "None"));
                            } else {
                                for (Map.Entry<String, String> parameterDescription : methodParameters.entrySet()) {
                                    functionPageData.addParagraphs(HTMLLikeParser.parseHTMLLike(null, " * " + parameterDescription.getKey() + " - " + parameterDescription.getValue()));
                                }
                            }

                            Collection<ParagraphData> returnDescription = HTMLLikeParser.parseHTMLLike(null, methodReturnDescriptions.get(methodName));
                            if (!returnDescription.isEmpty()) {
                                functionPageData.addParagraphs(paragraphWithSpaceBefore("Returns:"));
                                functionPageData.addParagraphs(returnDescription);
                            }

                            functionPageData.addParagraphs(methodAdditionalParagraphs.get(methodName));

                            defaultBrowserData.addEntry(modulePageId, functionPageData);
                        }
                    }

                    @Override
                    public void addObject(String object, ObjectDefinition objectDefinition, String objectDescription, Collection<ParagraphData> additionalParagraphs, Map<String, String> functionDescriptions, Map<String, Map<String, String>> functionParametersDescriptions, Map<String, String> functionReturnDescriptions, Map<String, Collection<ParagraphData>> functionAdditionalParagraphs) {
                        // Ignore
                    }
                });
    }

    private static void buildBuiltinObjectPages(DefaultDocumentationData defaultBrowserData, ComputerLanguageContextInitializer computerLanguageContextInitializer) {
        computerLanguageContextInitializer.initializeContext(
                new ComputerLanguageContext() {
                    @Override
                    public void addObject(String object, ObjectDefinition objectDefinition, String objectDescription, Collection<ParagraphData> additionalParagraphs, Map<String, String> functionDescriptions, Map<String, Map<String, String>> functionParametersDescriptions, Map<String, String> functionReturnDescriptions, Map<String, Collection<ParagraphData>> functionAdditionalParagraphs) {
                        String objectPageId = getBuiltInObjectPageId(object);

                        PageData pageData = new PageData(objectPageId, "Variable - " + object, null);
                        pageData.addParagraphs(createTitleParagraph("Variable - " + object));
                        pageData.addParagraphs(HTMLLikeParser.parseHTMLLike(null, objectDescription));
                        pageData.addParagraphs(paragraphWithSpaceBefore("Functions:"));
                        for (String functionName : functionDescriptions.keySet()) {
                            pageData.addParagraphs(
                                    HTMLLikeParser.parseHTMLLike(null,
                                            " * <h navigate:" + getBuiltInObjectMethodPageId(object, functionName) + ">" + functionName + "()</h> - " + functionDescriptions.get(functionName)));
                        }

                        pageData.addParagraphs(additionalParagraphs);

                        defaultBrowserData.addEntry(null, pageData);

                        for (Map.Entry<String, String> functionEntry : functionDescriptions.entrySet()) {
                            String functionName = functionEntry.getKey();

                            PageData functionPageData = new PageData(getBuiltInObjectMethodPageId(object, functionName), functionName + "()", null);
                            functionPageData.addParagraphs(createTitleParagraph("Function - " + functionName));
                            functionPageData.addParagraphs(HTMLLikeParser.parseHTMLLike(null, functionEntry.getValue()));

                            functionPageData.addParagraphs(paragraphWithSpaceBefore("Parameters:"));

                            Map<String, String> functionParameters = functionParametersDescriptions.get(functionName);

                            if (functionParameters.isEmpty()) {
                                functionPageData.addParagraphs(HTMLLikeParser.parseHTMLLike(null, "None"));
                            }
                            for (Map.Entry<String, String> parameterDescription : functionParameters.entrySet()) {
                                functionPageData.addParagraphs(HTMLLikeParser.parseHTMLLike(null, " * " + parameterDescription.getKey() + " - " + parameterDescription.getValue()));
                            }

                            Collection<ParagraphData> returnDescription = HTMLLikeParser.parseHTMLLike(null, functionReturnDescriptions.get(functionName));
                            if (!returnDescription.isEmpty()) {
                                functionPageData.addParagraphs(paragraphWithSpaceBefore("Returns:"));
                                functionPageData.addParagraphs(returnDescription);
                            }

                            functionPageData.addParagraphs(functionAdditionalParagraphs.get(functionName));

                            defaultBrowserData.addEntry(objectPageId, functionPageData);
                        }
                    }

                    @Override
                    public void addComputerModule(ComputerModule computerModule, String description, Collection<ParagraphData> additionalParagraphs, Map<String, String> methodDescriptions, Map<String, Map<String, String>> methodParametersDescriptions, Map<String, String> methodReturnDescriptions, Map<String, Collection<ParagraphData>> methodAdditionalParagraphs) {
                        // Ignore
                    }
                });
    }

    private static PageData buildIntroductionPage(ComputerLanguageContextInitializer computerLanguageContextInitializer) {
        PageData pageData = new PageData("introduction", "Introduction", null);
        pageData.addParagraphs(createTitleParagraph("Introduction"));
        pageData.addParagraphs(HTMLLikeParser.parseHTMLLike(null, "This is the first page of the documentation."));

        pageData.addParagraphs(paragraphWithSpaceBefore("List of built-in objects:"));

        computerLanguageContextInitializer.initializeContext(
                new ComputerLanguageContext() {
                    @Override
                    public void addObject(String object, ObjectDefinition objectDefinition, String objectDescription, Collection<ParagraphData> additionalParagraphs, Map<String, String> functionDescriptions, Map<String, Map<String, String>> functionParametersDescriptions, Map<String, String> functionReturnDescriptions, Map<String, Collection<ParagraphData>> functionAdditionalParagraphs) {
                        pageData.addParagraphs(
                                HTMLLikeParser.parseHTMLLike(null,
                                        " * <h navigate:" + getBuiltInObjectPageId(object) + ">" + object + "</h> - " + objectDescription));
                    }

                    @Override
                    public void addComputerModule(ComputerModule computerModule, String description, Collection<ParagraphData> additionalParagraphs, Map<String, String> methodDescriptions, Map<String, Map<String, String>> methodParametersDescriptions, Map<String, String> methodReturnDescriptions, Map<String, Collection<ParagraphData>> methodAdditionalParagraphs) {
                        // Ignore
                    }
                });

        pageData.addParagraphs(paragraphWithSpaceBefore("List of registered computer modules:"));

        computerLanguageContextInitializer.initializeContext(
                new ComputerLanguageContext() {
                    @Override
                    public void addObject(String object, ObjectDefinition objectDefinition, String objectDescription, Collection<ParagraphData> additionalParagraphs, Map<String, String> functionDescriptions, Map<String, Map<String, String>> functionParametersDescriptions, Map<String, String> functionReturnDescriptions, Map<String, Collection<ParagraphData>> functionAdditionalParagraphs) {
                        // Ignore
                    }

                    @Override
                    public void addComputerModule(ComputerModule computerModule, String description, Collection<ParagraphData> additionalParagraphs, Map<String, String> methodDescriptions, Map<String, Map<String, String>> methodParametersDescriptions, Map<String, String> methodReturnDescriptions, Map<String, Collection<ParagraphData>> methodAdditionalParagraphs) {
                        pageData.addParagraphs(
                                HTMLLikeParser.parseHTMLLike(null,
                                        " * <h navigate:" + getComputerModulePageId(computerModule.getModuleType()) + ">" + computerModule.getModuleName() + "</h> - " + description));
                    }
                });

        return pageData;
    }

    private static Collection<ParagraphData> paragraphWithSpaceBefore(String text) {
        ParagraphRenderStyle renderStyle = new ParagraphRenderStyle() {
            @Override
            public Integer getParagraphIndentTop(boolean firstParagraph) {
                return 10;
            }
        };
        return HTMLLikeParser.parseHTMLLike(renderStyle, text);
    }

    private static Collection<ParagraphData> createTitleParagraph(String title) {
        return HTMLLikeParser.parseHTMLLike(null, "<f engine:title>" + title + "</f>");
    }

    public static Collection<ParagraphData> createExampleParagraphs(String description, String code) {
        List<ParagraphData> result = new LinkedList<>();
        result.addAll(paragraphWithSpaceBefore("Example:"));
        result.addAll(HTMLLikeParser.parseHTMLLike(null, "<h saveAs:example:" + HTMLLikeParser.encodeHTMLLike(code) + ">Save as example</h>"));
        result.addAll(
                HTMLLikeParser.parseHTMLLike(null, description));

        int maxCodeLineLength = 0;
        String[] lines = code.split("\n");
        StringBuilder codeEncoded = new StringBuilder();
        boolean first = true;
        for (String line : lines) {
            maxCodeLineLength = Math.max(maxCodeLineLength, line.length());
            if (!first) {
                codeEncoded.append("<l>");
            }
            codeEncoded.append(HTMLLikeParser.encodeHTMLLike(line));
            first = false;
        }

        final int finalMaxCodeLineLength = maxCodeLineLength;
        result.addAll(
                HTMLLikeParser.parseHTMLLike(
                        new ParagraphRenderStyle() {
                            @Override
                            public Integer getParagraphIndentTop(boolean firstParagraph) {
                                return 5;
                            }

                            @Override
                            public Integer getParagraphIndentBottom(boolean lastParagraph) {
                                return 5;
                            }

                            @Override
                            public Integer getParagraphIndentLeft() {
                                return 5;
                            }

                            @Override
                            public Integer getParagraphIndentRight() {
                                return 5;
                            }

                            @Override
                            public Integer getParagraphBackgroundIndentTop() {
                                return 3;
                            }

                            @Override
                            public Integer getParagraphBackgroundIndentBottom() {
                                return 3;
                            }

                            @Override
                            public Integer getParagraphBackgroundIndentLeft() {
                                return 3;
                            }

                            @Override
                            public Integer getParagraphBackgroundIndentRight() {
                                return 3;
                            }

                            @Override
                            public Color getParagraphBackground() {
                                return new Color(0.8f, 0.8f, 1f);
                            }

                            @Override
                            public Integer getParagraphMinimumWidth() {
                                return finalMaxCodeLineLength * 10;
                            }
                        },
                        "<f ModularComputers:november>" + codeEncoded.toString() + "</f>"));

        return result;
    }
}
