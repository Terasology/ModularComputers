// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.computer.ui.documentation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.computer.system.common.ComputerLanguageContext;
import org.terasology.computer.system.common.ComputerLanguageContextInitializer;
import org.terasology.computer.system.common.DocumentedFunctionExecutable;
import org.terasology.computer.system.common.DocumentedObjectDefinition;
import org.terasology.computer.system.server.lang.ComputerModule;
import org.terasology.computer.system.server.lang.ModuleMethodExecutable;
import org.terasology.engine.rendering.assets.font.Font;
import org.terasology.engine.rendering.nui.widgets.browser.data.ParagraphData;
import org.terasology.engine.rendering.nui.widgets.browser.data.basic.HTMLLikeParser;
import org.terasology.engine.rendering.nui.widgets.browser.ui.style.ContainerInteger;
import org.terasology.engine.rendering.nui.widgets.browser.ui.style.FixedContainerInteger;
import org.terasology.engine.rendering.nui.widgets.browser.ui.style.ParagraphRenderStyle;
import org.terasology.engine.utilities.Assets;
import org.terasology.nui.Color;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public final class DocumentationBuilder {
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

        defaultBrowserData.addEntry(null, buildIntroductionPage(computerLanguageContextInitializer), false);

        buildObjectTypePages(defaultBrowserData, computerLanguageContextInitializer);

        buildTopPages(defaultBrowserData, computerLanguageContextInitializer);
        buildBuiltinObjectPages(defaultBrowserData, "builtinObjects", computerLanguageContextInitializer);
        buildComputerModulePages(defaultBrowserData, "modules", computerLanguageContextInitializer);

        return defaultBrowserData;
    }

    private static void buildTopPages(DefaultDocumentationData defaultBrowserData,
                                      ComputerLanguageContextInitializer computerLanguageContextInitializer) {
        PageData modulesPage = new PageData("modules", null, "Modules", null);
        modulesPage.addParagraph(createTitleParagraph("Modules"));
        modulesPage.addParagraph(
                HTMLLikeParser.parseHTMLLikeParagraph(null, "Modules are special pieces of hardware that you could " +
                        "install in the computer " +
                        "in order to perform some very specific operations that are permitted by a module.<l>" +
                        "In order to use a module, after placing it in computer's inventory, you need to \"bind\" it " +
                        "in your code " +
                        "to a variable. For specific instructions on how to do it, please refer to " +
                        "<h navigate:" + getBuiltInObjectPageId("computer") + ">computer</h> built-in object " +
                        "documentation."));
        modulesPage.addParagraph(emphasizedParagraphWithSpaceBefore("List of available modules:"));

        PageData builtinObjectsPage = new PageData("builtinObjects", null, "Built-in Objects", null);
        builtinObjectsPage.addParagraph(createTitleParagraph("Built-in Objects"));
        builtinObjectsPage.addParagraph(
                HTMLLikeParser.parseHTMLLikeParagraph(null, "Built-in objects are objects (variables) that are preset" +
                        " in your script. " +
                        "These objects contain a set of valuable methods that allow you to do some basic things with " +
                        "your computer " +
                        "in game, that you would not be able to do otherwise."));
        builtinObjectsPage.addParagraph(emphasizedParagraphWithSpaceBefore("List of built-in objects:"));

        computerLanguageContextInitializer.initializeContext(
                new ComputerLanguageContext() {
                    @Override
                    public void addObjectType(String objectType, Collection<ParagraphData> documentation) {

                    }

                    @Override
                    public void addObject(String object, DocumentedObjectDefinition objectDefinition,
                                          String objectDescription,
                                          Collection<ParagraphData> additionalParagraphs) {
                        builtinObjectsPage.addParagraph(
                                HTMLLikeParser.parseHTMLLikeParagraph(null,
                                        " * <h navigate:" + getBuiltInObjectPageId(object) + ">" + object + "</h> - " + objectDescription));
                    }

                    @Override
                    public void addComputerModule(ComputerModule computerModule, String description,
                                                  Collection<ParagraphData> additionalParagraphs) {
                        modulesPage.addParagraph(
                                HTMLLikeParser.parseHTMLLikeParagraph(null,
                                        " * <h navigate:" + getComputerModulePageId(computerModule.getModuleType()) + ">" +
                                                computerModule.getModuleName() + "</h> - " + description));
                    }
                });

        defaultBrowserData.addEntry(null, modulesPage);
        defaultBrowserData.addEntry(null, builtinObjectsPage);
    }


    private static void buildObjectTypePages(DefaultDocumentationData defaultBrowserData,
                                             ComputerLanguageContextInitializer computerLanguageContextInitializer) {
        final String objectTypesPageId = "objectTypes";
        PageData objectTypesPage = new PageData(objectTypesPageId, null, "Object Types", null);
        objectTypesPage.addParagraph(createTitleParagraph("Object Types"));
        objectTypesPage.addParagraph(
                HTMLLikeParser.parseHTMLLikeParagraph(null, "There are multiple object types defined in the computer " +
                        "API. Some of them are built in, " +
                        "some of them are added by different modules."));
        objectTypesPage.addParagraph(emphasizedParagraphWithSpaceBefore("Object types:"));

        defaultBrowserData.addEntry(null, objectTypesPage);

        computerLanguageContextInitializer.initializeContext(
                new ComputerLanguageContext() {
                    @Override
                    public void addObjectType(String objectType, Collection<ParagraphData> documentation) {
                        String objectTypePageId = getObjectTypePageId(objectType);
                        objectTypesPage.addParagraph(HTMLLikeParser.parseHTMLLikeParagraph(null,
                                " * <h navigate:" + objectTypePageId + ">" + objectType + "</h>"));

                        PageData objectTypePage = new PageData(objectTypePageId, objectTypesPageId, objectType, null);
                        objectTypePage.addParagraph(createTitleParagraph("Object Type - " + objectType));
                        objectTypePage.addParagraphs(documentation);

                        defaultBrowserData.addEntry(objectTypesPageId, objectTypePage);
                    }

                    @Override
                    public void addComputerModule(ComputerModule computerModule, String description,
                                                  Collection<ParagraphData> additionalParagraphs) {
                        // Ignore
                    }

                    @Override
                    public void addObject(String object, DocumentedObjectDefinition objectDefinition,
                                          String objectDescription,
                                          Collection<ParagraphData> additionalParagraphs) {
                        // Ignore
                    }
                });
    }

    private static void buildComputerModulePages(DefaultDocumentationData defaultBrowserData, String parentId,
                                                 ComputerLanguageContextInitializer computerLanguageContextInitializer) {
        computerLanguageContextInitializer.initializeContext(
                new ComputerLanguageContext() {
                    @Override
                    public void addComputerModule(ComputerModule computerModule, String description,
                                                  Collection<ParagraphData> additionalParagraphs) {
                        Map<String, String> methodSimpleDescriptions = new TreeMap<>();
                        Map<String, Collection<ParagraphData>> methodPageDescriptions = new TreeMap<>();
                        Map<String, Map<String, String>> methodParametersDescriptions = new HashMap<>();
                        Map<String, String> methodReturnDescriptions = new HashMap<>();
                        Map<String, Collection<Collection<ParagraphData>>> methodExamples = new HashMap<>();

                        for (Map.Entry<String, ModuleMethodExecutable<?>> methodEntry :
                                computerModule.getAllMethods().entrySet()) {
                            String methodName = methodEntry.getKey();
                            ModuleMethodExecutable<?> method = methodEntry.getValue();
                            MethodDocumentation methodDocumentation = method.getMethodDocumentation();

                            methodSimpleDescriptions.put(methodName,
                                    methodDocumentation.getHTMLLikeSimpleDocumentation());
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

                        PageData pageData = new PageData(modulePageId, parentId,
                                "Module - " + computerModule.getModuleName(), null);
                        pageData.addParagraph(createTitleParagraph("Module - " + computerModule.getModuleName()));
                        pageData.addParagraph(HTMLLikeParser.parseHTMLLikeParagraph(null, description));
                        pageData.addParagraph(emphasizedParagraphWithSpaceBefore("Methods:"));
                        for (String methodName : methodSimpleDescriptions.keySet()) {
                            pageData.addParagraph(
                                    HTMLLikeParser.parseHTMLLikeParagraph(null,
                                            " * <h navigate:" + getComputerModuleMethodPageId(moduleType, methodName) + ">" + methodName + "()</h> - " +
                                                    methodSimpleDescriptions.get(methodName)));
                        }

                        pageData.addParagraphs(additionalParagraphs);

                        defaultBrowserData.addEntry(parentId, pageData);

                        for (Map.Entry<String, String> methodEntry : methodSimpleDescriptions.entrySet()) {
                            String methodName = methodEntry.getKey();

                            PageData functionPageData = new PageData(getComputerModuleMethodPageId(moduleType,
                                    methodName), modulePageId, methodName + "()", null);
                            functionPageData.addParagraph(createTitleParagraph("Method - " + methodName));
                            functionPageData.addParagraph(HTMLLikeParser.parseHTMLLikeParagraph(null,
                                    methodEntry.getValue()));

                            functionPageData.addParagraph(emphasizedParagraphWithSpaceBefore("Parameters:"));

                            Map<String, String> methodParameters = methodParametersDescriptions.get(methodName);

                            if (methodParameters == null || methodParameters.isEmpty()) {
                                functionPageData.addParagraph(HTMLLikeParser.parseHTMLLikeParagraph(null, "None"));
                            } else {
                                for (Map.Entry<String, String> parameterDescription : methodParameters.entrySet()) {
                                    functionPageData.addParagraph(HTMLLikeParser.parseHTMLLikeParagraph(null,
                                            " * " + parameterDescription.getKey() + " - " +
                                            parameterDescription.getValue()));
                                }
                            }

                            ParagraphData returnDescription = HTMLLikeParser.parseHTMLLikeParagraph(null,
                                    methodReturnDescriptions.get(methodName));
                            if (returnDescription != null) {
                                functionPageData.addParagraph(emphasizedParagraphWithSpaceBefore("Returns:"));
                                functionPageData.addParagraph(returnDescription);
                            }


                            Collection<Collection<ParagraphData>> examples = methodExamples.get(methodName);
                            if (!examples.isEmpty()) {
                                functionPageData.addParagraph(emphasizedParagraphWithSpaceBefore("Examples:"));
                                for (Collection<ParagraphData> exampleData : examples) {
                                    functionPageData.addParagraphs(exampleData);
                                }
                            } else {
                                logger.warn("Unable to find any example for: module - " + moduleType + ", method - " + methodName);
                            }

                            defaultBrowserData.addEntry(modulePageId, functionPageData);
                        }
                    }

                    @Override
                    public void addObjectType(String objectType, Collection<ParagraphData> documentation) {
                        // Ignore
                    }

                    @Override
                    public void addObject(String object, DocumentedObjectDefinition objectDefinition,
                                          String objectDescription,
                                          Collection<ParagraphData> additionalParagraphs) {
                        // Ignore
                    }
                });
    }

    private static String linkObjectTypeIfPageAvailable(String objectType,
                                                        DefaultDocumentationData defaultBrowserData) {
        if (objectType.startsWith("Array of ")) {
            return linkObjectTypeIfPageAvailable("Array", defaultBrowserData) + " of " +
                    linkObjectTypeIfPageAvailable(objectType.substring(9), defaultBrowserData);
        } else {
            String objectTypePageId = getObjectTypePageId(objectType);
            if (defaultBrowserData.getDocument(objectTypePageId) != null) {
                return "<h navigate:" + objectTypePageId + ">" + objectType + "</h>";
            } else {
                logMissingObjectType(objectType);
                return objectType;
            }
        }
    }

    private static void logMissingObjectType(String objectType) {
        if (!objectType.equals("any")) {
            logger.warn("Unable to find documentation for object type - " + objectType);
        }
    }

    private static void buildBuiltinObjectPages(DefaultDocumentationData defaultBrowserData, String parentId,
                                                ComputerLanguageContextInitializer computerLanguageContextInitializer) {
        computerLanguageContextInitializer.initializeContext(
                new ComputerLanguageContext() {
                    @Override
                    public void addObject(String object, DocumentedObjectDefinition objectDefinition,
                                          String objectDescription,
                                          Collection<ParagraphData> additionalParagraphs) {
                        Map<String, String> methodSimpleDescriptions = new TreeMap<>();
                        Map<String, Collection<ParagraphData>> methodPageDescriptions = new TreeMap<>();
                        Map<String, Map<String, String>> methodParametersDescriptions = new HashMap<>();
                        Map<String, String> methodReturnDescriptions = new HashMap<>();
                        Map<String, Collection<Collection<ParagraphData>>> methodExamples = new HashMap<>();

                        for (String methodName : objectDefinition.getMethodNames()) {
                            DocumentedFunctionExecutable documentedFunctionExecutable =
                                    objectDefinition.getMethod(methodName);
                            MethodDocumentation methodDocumentation =
                                    documentedFunctionExecutable.getMethodDocumentation();

                            methodSimpleDescriptions.put(methodName,
                                    methodDocumentation.getHTMLLikeSimpleDocumentation());
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

                        PageData pageData = new PageData(objectPageId, parentId, "Variable - " + object, null);
                        pageData.addParagraph(createTitleParagraph("Variable - " + object));
                        pageData.addParagraph(HTMLLikeParser.parseHTMLLikeParagraph(null, objectDescription));
                        pageData.addParagraph(emphasizedParagraphWithSpaceBefore("Functions:"));
                        for (String functionName : methodSimpleDescriptions.keySet()) {
                            pageData.addParagraph(
                                    HTMLLikeParser.parseHTMLLikeParagraph(null,
                                            " * <h navigate:" + getBuiltInObjectMethodPageId(object, functionName) + 
                                                    ">" + functionName + "()</h> - " +
                                                    methodSimpleDescriptions.get(functionName)));
                        }

                        pageData.addParagraphs(additionalParagraphs);

                        defaultBrowserData.addEntry(parentId, pageData);

                        for (Map.Entry<String, String> functionEntry : methodSimpleDescriptions.entrySet()) {
                            String methodName = functionEntry.getKey();

                            PageData functionPageData = new PageData(getBuiltInObjectMethodPageId(object, methodName)
                                    , objectPageId, methodName + "()", null);
                            functionPageData.addParagraph(createTitleParagraph("Function - " + methodName));
                            functionPageData.addParagraphs(methodPageDescriptions.get(methodName));

                            functionPageData.addParagraph(emphasizedParagraphWithSpaceBefore("Parameters:"));

                            Map<String, String> functionParameters = methodParametersDescriptions.get(methodName);

                            if (functionParameters.isEmpty()) {
                                functionPageData.addParagraph(HTMLLikeParser.parseHTMLLikeParagraph(null, "None"));
                            }
                            for (Map.Entry<String, String> parameterDescription : functionParameters.entrySet()) {
                                functionPageData.addParagraph(HTMLLikeParser.parseHTMLLikeParagraph(null,
                                        " * " + parameterDescription.getKey() + " - " +
                                        parameterDescription.getValue()));
                            }

                            ParagraphData returnDescription = HTMLLikeParser.parseHTMLLikeParagraph(null,
                                    methodReturnDescriptions.get(methodName));
                            if (returnDescription != null) {
                                functionPageData.addParagraph(emphasizedParagraphWithSpaceBefore("Returns:"));
                                functionPageData.addParagraph(returnDescription);
                            }

                            Collection<Collection<ParagraphData>> examples = methodExamples.get(methodName);
                            if (!examples.isEmpty()) {
                                functionPageData.addParagraph(emphasizedParagraphWithSpaceBefore("Examples:"));
                                for (Collection<ParagraphData> exampleData : examples) {
                                    functionPageData.addParagraphs(exampleData);
                                }
                            } else {
                                logger.warn("Unable to find any example for: built-in object - " + object + ", method" +
                                        " - " + methodName);
                            }

                            defaultBrowserData.addEntry(objectPageId, functionPageData);
                        }
                    }

                    @Override
                    public void addObjectType(String objectType, Collection<ParagraphData> documentation) {
                        // Ignore
                    }

                    @Override
                    public void addComputerModule(ComputerModule computerModule, String description,
                                                  Collection<ParagraphData> additionalParagraphs) {
                        // Ignore
                    }
                });
    }

    private static PageData buildIntroductionPage(ComputerLanguageContextInitializer computerLanguageContextInitializer) {
        PageData pageData = new PageData("introduction", null, "Introduction", null);
        pageData.addParagraph(createTitleParagraph("Introduction"));
        pageData.addParagraph(HTMLLikeParser.parseHTMLLikeParagraph(null, "This is the first page of the " +
                "documentation."));

        return pageData;
    }

    private static ParagraphData emphasizedParagraphWithSpaceBefore(String text) {
        ParagraphRenderStyle renderStyle = new ParagraphRenderStyle() {
            @Override
            public ContainerInteger getParagraphMarginTop() {
                return new FixedContainerInteger(10);
            }

            @Override
            public Font getFont(boolean hyperlink) {
                return Assets.getFont("engine:NotoSans-Bold").get();
            }
        };
        return HTMLLikeParser.parseHTMLLikeParagraph(renderStyle, text);
    }

    private static ParagraphData createTitleParagraph(String title) {
        return HTMLLikeParser.parseHTMLLikeParagraph(null, "<f engine:NotoSans-Regular-Title>" + title + "</f>");
    }

    public static Collection<ParagraphData> createExampleParagraphs(String description, String code) {
        List<ParagraphData> result = new LinkedList<>();
        result.add(HTMLLikeParser.parseHTMLLikeParagraph(null,
                "<h saveAs:example:" + HTMLLikeParser.encodeHTMLLike(code) + ">Save as example</h>"));
        result.add(
                HTMLLikeParser.parseHTMLLikeParagraph(null, description));

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
        result.add(
                HTMLLikeParser.parseHTMLLikeParagraph(
                        new ParagraphRenderStyle() {
                            @Override
                            public ContainerInteger getParagraphMarginTop() {
                                return new FixedContainerInteger(5);
                            }

                            @Override
                            public ContainerInteger getParagraphMarginBottom() {
                                return new FixedContainerInteger(5);
                            }

                            @Override
                            public ContainerInteger getParagraphMarginLeft() {
                                return new FixedContainerInteger(5);
                            }

                            @Override
                            public ContainerInteger getParagraphMarginRight() {
                                return new FixedContainerInteger(5);
                            }

                            @Override
                            public ContainerInteger getParagraphPaddingTop() {
                                return new FixedContainerInteger(3);
                            }

                            @Override
                            public ContainerInteger getParagraphPaddingBottom() {
                                return new FixedContainerInteger(3);
                            }

                            @Override
                            public ContainerInteger getParagraphPaddingLeft() {
                                return new FixedContainerInteger(3);
                            }

                            @Override
                            public ContainerInteger getParagraphPaddingRight() {
                                return new FixedContainerInteger(3);
                            }

                            @Override
                            public Color getParagraphBackground() {
                                return new Color(0.8f, 0.8f, 1f);
                            }

                            @Override
                            public ContainerInteger getParagraphMinimumWidth() {
                                return new FixedContainerInteger(finalMaxCodeLineLength * 8);
                            }
                        },
                        "<f ModularComputers:november>" + codeEncoded.toString() + "</f>"));

        return result;
    }
}
