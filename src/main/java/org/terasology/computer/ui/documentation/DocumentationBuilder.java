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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.asset.Assets;
import org.terasology.browser.data.ParagraphData;
import org.terasology.browser.data.basic.HTMLLikeParser;
import org.terasology.browser.ui.style.ParagraphRenderStyle;
import org.terasology.computer.system.common.ComputerLanguageContext;
import org.terasology.computer.system.common.ComputerLanguageContextInitializer;
import org.terasology.computer.system.common.DocumentedFunctionExecutable;
import org.terasology.computer.system.common.DocumentedObjectDefinition;
import org.terasology.computer.system.server.lang.ComputerModule;
import org.terasology.computer.system.server.lang.ModuleMethodExecutable;
import org.terasology.rendering.assets.font.Font;
import org.terasology.rendering.nui.Color;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class DocumentationBuilder {
    private static final Logger logger = LoggerFactory.getLogger(DocumentationBuilder.class);

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

    public static String getObjectTypePageId(String objectType) {
        return "objectTypes-" + objectType;
    }

    public static DefaultDocumentationData buildDocumentation(ComputerLanguageContextInitializer computerLanguageContextInitializer) {
        DefaultDocumentationData defaultBrowserData = new DefaultDocumentationData();

        defaultBrowserData.addEntry(null, buildIntroductionPage(computerLanguageContextInitializer));

        buildObjectTypePages(defaultBrowserData, computerLanguageContextInitializer);

        buildBuiltinObjectPages(defaultBrowserData, computerLanguageContextInitializer);
        buildComputerModulePages(defaultBrowserData, computerLanguageContextInitializer);

        return defaultBrowserData;
    }


    private static void buildObjectTypePages(DefaultDocumentationData defaultBrowserData, ComputerLanguageContextInitializer computerLanguageContextInitializer) {
        PageData objectTypesPage = new PageData("objectTypes", "Object Types", null);
        objectTypesPage.addParagraphs(createTitleParagraph("Object Types"));
        objectTypesPage.addParagraphs(
                HTMLLikeParser.parseHTMLLike(null, "There are multiple object types defined in the computer API. Some of them are built in, " +
                        "some of them are added by different modules."));
        objectTypesPage.addParagraphs(emphasizedParagraphWithSpaceBefore("Object types:"));

        defaultBrowserData.addEntry(null, objectTypesPage);

        computerLanguageContextInitializer.initializeContext(
                new ComputerLanguageContext() {
                    @Override
                    public void addObjectType(String objectType, Collection<ParagraphData> documentation) {
                        String objectTypePageId = getObjectTypePageId(objectType);
                        objectTypesPage.addParagraphs(HTMLLikeParser.parseHTMLLike(null,
                                " * <h navigate:" + objectTypePageId + ">" + objectType + "</h>"));

                        PageData objectTypePage = new PageData(objectTypePageId, objectType, null);
                        objectTypePage.addParagraphs(createTitleParagraph("Object Type - " + objectType));
                        objectTypePage.addParagraphs(documentation);

                        defaultBrowserData.addEntry("objectTypes", objectTypePage);
                    }

                    @Override
                    public void addComputerModule(ComputerModule computerModule, String description, Collection<ParagraphData> additionalParagraphs) {
                        // Ignore
                    }

                    @Override
                    public void addObject(String object, DocumentedObjectDefinition objectDefinition, String objectDescription, Collection<ParagraphData> additionalParagraphs) {
                        // Ignore
                    }
                });
    }

    private static void buildComputerModulePages(DefaultDocumentationData defaultBrowserData, ComputerLanguageContextInitializer computerLanguageContextInitializer) {
        computerLanguageContextInitializer.initializeContext(
                new ComputerLanguageContext() {
                    @Override
                    public void addComputerModule(ComputerModule computerModule, String description, Collection<ParagraphData> additionalParagraphs) {
                        Map<String, String> methodSimpleDescriptions = new TreeMap<>();
                        Map<String, Collection<ParagraphData>> methodPageDescriptions = new TreeMap<>();
                        Map<String, Map<String, String>> methodParametersDescriptions = new HashMap<>();
                        Map<String, String> methodReturnDescriptions = new HashMap<>();
                        Map<String, Iterable<Collection<ParagraphData>>> methodExamples = new HashMap<>();

                        for (Map.Entry<String, ModuleMethodExecutable<?>> methodEntry : computerModule.getAllMethods().entrySet()) {
                            String methodName = methodEntry.getKey();
                            ModuleMethodExecutable<?> method = methodEntry.getValue();
                            MethodDocumentation methodDocumentation = method.getMethodDocumentation();

                            methodSimpleDescriptions.put(methodName, methodDocumentation.getHTMLLikeSimpleDocumentation());
                            methodPageDescriptions.put(methodName, methodDocumentation.getPageDocumentation());

                            Map<String, String> parameterDescriptions = new LinkedHashMap<>();
                            for (String parameterName : method.getParameterNames()) {
                                String parameterType = methodDocumentation.getParameterType(parameterName);
                                parameterType = linkObjectTypeIfPageAvailable(parameterType, defaultBrowserData);
                                parameterDescriptions.put(parameterName, "[" + parameterType + "] "
                                        + methodDocumentation.getHTMLLikeParameterDocumentation(parameterName));
                            }
                            methodParametersDescriptions.put(methodName, parameterDescriptions);

                            if (methodDocumentation.getReturnType() != null) {
                                String returnType = methodDocumentation.getReturnType();
                                returnType = linkObjectTypeIfPageAvailable(returnType, defaultBrowserData);

                                methodReturnDescriptions.put(methodName, "[" + returnType + "] "
                                        + methodDocumentation.getHTMLLikeReturnDocumentation());
                            }

                            if (methodDocumentation.getExamples() != null) {
                                methodExamples.put(methodName, methodDocumentation.getExamples());
                            }
                        }


                        String moduleType = computerModule.getModuleType();
                        String modulePageId = getComputerModulePageId(moduleType);

                        PageData pageData = new PageData(modulePageId, "Module - " + computerModule.getModuleName(), null);
                        pageData.addParagraphs(createTitleParagraph("Module - " + computerModule.getModuleName()));
                        pageData.addParagraphs(HTMLLikeParser.parseHTMLLike(null, description));
                        pageData.addParagraphs(emphasizedParagraphWithSpaceBefore("Methods:"));
                        for (String methodName : methodSimpleDescriptions.keySet()) {
                            pageData.addParagraphs(
                                    HTMLLikeParser.parseHTMLLike(null,
                                            " * <h navigate:" + getComputerModuleMethodPageId(moduleType, methodName) + ">" + methodName + "()</h> - " + methodSimpleDescriptions.get(methodName)));
                        }

                        pageData.addParagraphs(additionalParagraphs);

                        defaultBrowserData.addEntry(null, pageData);

                        for (Map.Entry<String, String> methodEntry : methodSimpleDescriptions.entrySet()) {
                            String methodName = methodEntry.getKey();

                            PageData functionPageData = new PageData(getComputerModuleMethodPageId(moduleType, methodName), methodName + "()", null);
                            functionPageData.addParagraphs(createTitleParagraph("Method - " + methodName));
                            functionPageData.addParagraphs(HTMLLikeParser.parseHTMLLike(null, methodEntry.getValue()));

                            functionPageData.addParagraphs(emphasizedParagraphWithSpaceBefore("Parameters:"));

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
                                functionPageData.addParagraphs(emphasizedParagraphWithSpaceBefore("Returns:"));
                                functionPageData.addParagraphs(returnDescription);
                            }


                            functionPageData.addParagraphs(emphasizedParagraphWithSpaceBefore("Examples:"));
                            for (Collection<ParagraphData> exampleData : methodExamples.get(methodName)) {
                                functionPageData.addParagraphs(exampleData);
                            }

                            defaultBrowserData.addEntry(modulePageId, functionPageData);
                        }
                    }

                    @Override
                    public void addObjectType(String objectType, Collection<ParagraphData> documentation) {
                        // Ignore
                    }

                    @Override
                    public void addObject(String object, DocumentedObjectDefinition objectDefinition, String objectDescription, Collection<ParagraphData> additionalParagraphs) {
                        // Ignore
                    }
                });
    }

    private static String linkObjectTypeIfPageAvailable(String objectType, DefaultDocumentationData defaultBrowserData) {
        if (objectType.startsWith("Array of ")) {
            return linkObjectTypeIfPageAvailable("Array", defaultBrowserData) + " of " +
                    linkObjectTypeIfPageAvailable(objectType.substring(9), defaultBrowserData);
        } else {
            String objectTypePageId = getObjectTypePageId(objectType);
            if (defaultBrowserData.getDocument(objectTypePageId) != null) {
                objectType = "<h navigate:" + objectTypePageId + ">" + objectType + "</h>";
            } else {
                logMissingObjectType(objectType);
            }
        }
        return objectType;
    }

    private static void logMissingObjectType(String objectType) {
        if (!objectType.equals("any")) {
            logger.warn("Unable to find documentation for object type - " + objectType);
        }
    }

    private static void buildBuiltinObjectPages(DefaultDocumentationData defaultBrowserData, ComputerLanguageContextInitializer computerLanguageContextInitializer) {
        computerLanguageContextInitializer.initializeContext(
                new ComputerLanguageContext() {
                    @Override
                    public void addObject(String object, DocumentedObjectDefinition objectDefinition, String objectDescription, Collection<ParagraphData> additionalParagraphs) {
                        Map<String, String> methodSimpleDescriptions = new TreeMap<>();
                        Map<String, Collection<ParagraphData>> methodPageDescriptions = new TreeMap<>();
                        Map<String, Map<String, String>> methodParametersDescriptions = new HashMap<>();
                        Map<String, String> methodReturnDescriptions = new HashMap<>();
                        Map<String, Iterable<Collection<ParagraphData>>> methodExamples = new HashMap<>();

                        for (String methodName : objectDefinition.getMethodNames()) {
                            DocumentedFunctionExecutable documentedFunctionExecutable = objectDefinition.getMethod(methodName);
                            MethodDocumentation methodDocumentation = documentedFunctionExecutable.getMethodDocumentation();

                            methodSimpleDescriptions.put(methodName, methodDocumentation.getHTMLLikeSimpleDocumentation());
                            methodPageDescriptions.put(methodName, methodDocumentation.getPageDocumentation());

                            Map<String, String> parameterDescriptions = new LinkedHashMap<>();
                            for (String parameterName : documentedFunctionExecutable.getParameterNames()) {
                                String parameterType = methodDocumentation.getParameterType(parameterName);
                                parameterType = linkObjectTypeIfPageAvailable(parameterType, defaultBrowserData);
                                parameterDescriptions.put(parameterName, "[" + parameterType + "] "
                                        + methodDocumentation.getHTMLLikeParameterDocumentation(parameterName));
                            }
                            methodParametersDescriptions.put(methodName, parameterDescriptions);

                            if (methodDocumentation.getReturnType() != null) {
                                String returnType = methodDocumentation.getReturnType();
                                returnType = linkObjectTypeIfPageAvailable(returnType, defaultBrowserData);

                                methodReturnDescriptions.put(methodName, "[" + returnType + "] "
                                        + methodDocumentation.getHTMLLikeReturnDocumentation());
                            }

                            if (methodDocumentation.getExamples() != null) {
                                methodExamples.put(methodName, methodDocumentation.getExamples());
                            }
                        }


                        String objectPageId = getBuiltInObjectPageId(object);

                        PageData pageData = new PageData(objectPageId, "Variable - " + object, null);
                        pageData.addParagraphs(createTitleParagraph("Variable - " + object));
                        pageData.addParagraphs(HTMLLikeParser.parseHTMLLike(null, objectDescription));
                        pageData.addParagraphs(emphasizedParagraphWithSpaceBefore("Functions:"));
                        for (String functionName : methodSimpleDescriptions.keySet()) {
                            pageData.addParagraphs(
                                    HTMLLikeParser.parseHTMLLike(null,
                                            " * <h navigate:" + getBuiltInObjectMethodPageId(object, functionName) + ">" + functionName + "()</h> - " + methodSimpleDescriptions.get(functionName)));
                        }

                        pageData.addParagraphs(additionalParagraphs);

                        defaultBrowserData.addEntry(null, pageData);

                        for (Map.Entry<String, String> functionEntry : methodSimpleDescriptions.entrySet()) {
                            String methodName = functionEntry.getKey();

                            PageData functionPageData = new PageData(getBuiltInObjectMethodPageId(object, methodName), methodName + "()", null);
                            functionPageData.addParagraphs(createTitleParagraph("Function - " + methodName));
                            functionPageData.addParagraphs(methodPageDescriptions.get(methodName));

                            functionPageData.addParagraphs(emphasizedParagraphWithSpaceBefore("Parameters:"));

                            Map<String, String> functionParameters = methodParametersDescriptions.get(methodName);

                            if (functionParameters.isEmpty()) {
                                functionPageData.addParagraphs(HTMLLikeParser.parseHTMLLike(null, "None"));
                            }
                            for (Map.Entry<String, String> parameterDescription : functionParameters.entrySet()) {
                                functionPageData.addParagraphs(HTMLLikeParser.parseHTMLLike(null, " * " + parameterDescription.getKey() + " - " + parameterDescription.getValue()));
                            }

                            Collection<ParagraphData> returnDescription = HTMLLikeParser.parseHTMLLike(null, methodReturnDescriptions.get(methodName));
                            if (!returnDescription.isEmpty()) {
                                functionPageData.addParagraphs(emphasizedParagraphWithSpaceBefore("Returns:"));
                                functionPageData.addParagraphs(returnDescription);
                            }

                            functionPageData.addParagraphs(emphasizedParagraphWithSpaceBefore("Examples:"));
                            for (Collection<ParagraphData> exampleData : methodExamples.get(methodName)) {
                                functionPageData.addParagraphs(exampleData);
                            }

                            defaultBrowserData.addEntry(objectPageId, functionPageData);
                        }
                    }

                    @Override
                    public void addObjectType(String objectType, Collection<ParagraphData> documentation) {
                        // Ignore
                    }

                    @Override
                    public void addComputerModule(ComputerModule computerModule, String description, Collection<ParagraphData> additionalParagraphs) {
                        // Ignore
                    }
                });
    }

    private static PageData buildIntroductionPage(ComputerLanguageContextInitializer computerLanguageContextInitializer) {
        PageData pageData = new PageData("introduction", "Introduction", null);
        pageData.addParagraphs(createTitleParagraph("Introduction"));
        pageData.addParagraphs(HTMLLikeParser.parseHTMLLike(null, "This is the first page of the documentation."));

        pageData.addParagraphs(emphasizedParagraphWithSpaceBefore("List of built-in objects:"));

        computerLanguageContextInitializer.initializeContext(
                new ComputerLanguageContext() {
                    @Override
                    public void addObject(String object, DocumentedObjectDefinition objectDefinition, String objectDescription, Collection<ParagraphData> additionalParagraphs) {
                        pageData.addParagraphs(
                                HTMLLikeParser.parseHTMLLike(null,
                                        " * <h navigate:" + getBuiltInObjectPageId(object) + ">" + object + "</h> - " + objectDescription));
                    }

                    @Override
                    public void addObjectType(String objectType, Collection<ParagraphData> documentation) {
                        // Ignore
                    }

                    @Override
                    public void addComputerModule(ComputerModule computerModule, String description, Collection<ParagraphData> additionalParagraphs) {
                        // Ignore
                    }
                });

        pageData.addParagraphs(emphasizedParagraphWithSpaceBefore("List of registered computer modules:"));

        computerLanguageContextInitializer.initializeContext(
                new ComputerLanguageContext() {
                    @Override
                    public void addObject(String object, DocumentedObjectDefinition objectDefinition, String objectDescription, Collection<ParagraphData> additionalParagraphs) {
                        // Ignore
                    }

                    @Override
                    public void addObjectType(String objectType, Collection<ParagraphData> documentation) {
                        // Ignore
                    }

                    @Override
                    public void addComputerModule(ComputerModule computerModule, String description, Collection<ParagraphData> additionalParagraphs) {
                        pageData.addParagraphs(
                                HTMLLikeParser.parseHTMLLike(null,
                                        " * <h navigate:" + getComputerModulePageId(computerModule.getModuleType()) + ">" + computerModule.getModuleName() + "</h> - " + description));
                    }
                });

        return pageData;
    }

    private static Collection<ParagraphData> emphasizedParagraphWithSpaceBefore(String text) {
        ParagraphRenderStyle renderStyle = new ParagraphRenderStyle() {
            @Override
            public Integer getParagraphIndentTop(boolean firstParagraph) {
                return 10;
            }

            @Override
            public Font getFont(boolean hyperlink) {
                return Assets.getFont("engine:NotoSans-Bold");
            }
        };
        return HTMLLikeParser.parseHTMLLike(renderStyle, text);
    }

    private static Collection<ParagraphData> createTitleParagraph(String title) {
        return HTMLLikeParser.parseHTMLLike(null, "<f engine:title>" + title + "</f>");
    }

    public static Collection<ParagraphData> createExampleParagraphs(String description, String code) {
        List<ParagraphData> result = new LinkedList<>();
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
                                return finalMaxCodeLineLength * 8;
                            }
                        },
                        "<f ModularComputers:november>" + codeEncoded.toString() + "</f>"));

        return result;
    }
}
