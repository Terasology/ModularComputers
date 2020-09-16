// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.modularcomputers.system.common;

import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterMode;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.registry.Share;
import org.terasology.engine.rendering.nui.widgets.browser.data.ParagraphData;
import org.terasology.engine.rendering.nui.widgets.browser.data.basic.HTMLLikeParser;
import org.terasology.modularcomputers.system.server.lang.ComputerModule;
import org.terasology.modularcomputers.system.server.lang.computer.BindFirstModuleOfTypeFunction;
import org.terasology.modularcomputers.system.server.lang.computer.BindModuleFunction;
import org.terasology.modularcomputers.system.server.lang.computer.GetModuleSlotCountFunction;
import org.terasology.modularcomputers.system.server.lang.computer.GetModuleTypeFunction;
import org.terasology.modularcomputers.system.server.lang.console.AppendToConsoleFunction;
import org.terasology.modularcomputers.system.server.lang.console.ClearConsoleFunction;
import org.terasology.modularcomputers.system.server.lang.console.WriteToConsoleFunction;
import org.terasology.modularcomputers.system.server.lang.os.AllFunction;
import org.terasology.modularcomputers.system.server.lang.os.AnyFunction;
import org.terasology.modularcomputers.system.server.lang.os.CreateSleepMsFunction;
import org.terasology.modularcomputers.system.server.lang.os.CreateSleepTickFunction;
import org.terasology.modularcomputers.system.server.lang.os.FormatFunction;
import org.terasology.modularcomputers.system.server.lang.os.ParseFloatFunction;
import org.terasology.modularcomputers.system.server.lang.os.ParseIntFunction;
import org.terasology.modularcomputers.system.server.lang.os.TypeOfFunction;
import org.terasology.modularcomputers.system.server.lang.os.WaitForFunction;
import org.terasology.modularcomputers.ui.documentation.DocumentationBuilder;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

@RegisterSystem(RegisterMode.ALWAYS)
@Share(value = {ComputerLanguageRegistry.class, ComputerModuleRegistry.class, ComputerLanguageContextInitializer.class})
public class ComputerLanguageCommonSystem extends BaseComponentSystem implements ComputerLanguageRegistry,
        ComputerModuleRegistry, ComputerLanguageContextInitializer {

    private final Map<String, Collection<ParagraphData>> objectTypes = new TreeMap<>();

    private final Map<String, MapObjectDefinition> objectDefinitions = new TreeMap<>();
    private final Map<String, String> objectDescriptions = new HashMap<>();
    private final Map<String, Collection<ParagraphData>> objectAdditionalParagraphs = new HashMap<>();

    private final Map<String, ComputerModule> computerModuleRegistry = new HashMap<>();
    private final Map<String, ComputerModule> computerModulesByName = new TreeMap<>();
    private final Map<String, String> computerModuleDescriptions = new HashMap<>();
    private final Map<String, Collection<ParagraphData>> computerModuleAdditionalParagraphs = new HashMap<>();

    @Override
    public void initialise() {
        registerObjectType("Array", Collections.singleton(HTMLLikeParser.parseHTMLLikeParagraph(null, "A list of " +
                "objects. Objects in the array may be of any type, unless a method " +
                "requires to pass array of objects of a specific type. To create an array you can use the following " +
                "syntax:<l>" +
                "var array = [1, \"a\", true, null];<l>" +
                "To access elements of a returned array, you have to use [n] notation, so to extract second (0-based)" +
                " element from an array do the following:<l>" +
                "var result = array[1];<l><l>" +
                "Array has three built in methods:<l>" +
                "* add(any) - adds the specified object to the end of the array,<l>" +
                "* remove(Number) - removes an object at the specified index from an array,<l>" +
                "* size() - returns the size (length) of the array, so number of elements it has.")));
        registerObjectType("Map", Collections.singleton(HTMLLikeParser.parseHTMLLikeParagraph(null, "An association " +
                "map of Strings to objects of any type. To create an array, use the following syntax:<l>" +
                "var map = {\"property1\": 1, \"property2\": \"a\", \"property3\": true };<l>" +
                "To access elements of a map, use the [\"propertyName\"] notation, so for example to get value in a " +
                "map for property \"p\" do the following:<l>" +
                "var result = map[\"p\"];<l>" +
                "Map has one built in method:<l>" +
                "* size() - returns the size of the map, so number of properties it has defined.")));

        registerObjectType("String", Collections.singleton(HTMLLikeParser.parseHTMLLikeParagraph(null, "Object " +
                "representing a piece of text.")));
        registerObjectType("Number", Collections.singleton(HTMLLikeParser.parseHTMLLikeParagraph(null, "Object " +
                "representing a number, either fixed point or floating point. " +
                "All variables of this type are always treated as if they were floating point, however if for some " +
                "reason a whole number is " +
                "needed, the value is rounded down.")));
        registerObjectType("Boolean", Collections.singleton(HTMLLikeParser.parseHTMLLikeParagraph(null, "Object " +
                "representing a boolean value, it may have two possible value - true and false.")));
        registerObjectType("Object", Collections.singleton(HTMLLikeParser.parseHTMLLikeParagraph(null, "Object is a " +
                "special type of object, methods it has are defined in the documentation of the method " +
                "that has returned it.")));

        registerObjectType("Direction", Collections.singleton(HTMLLikeParser.parseHTMLLikeParagraph(null, "Direction " +
                "is a special type of <h navigate:" +
                DocumentationBuilder.getObjectTypePageId("String") + ">String</h> " +
                "that specifies a direction, usually in relation to computer. It can have 6 possible values:<l>" +
                "* up - above the computer,<l>" +
                "* down - below the computer,<l>" +
                "* east - towards negative x,<l>" +
                "* west - towards positive x,<l>" +
                "* north - towards negative z,<l>" +
                "* south - towards positive z.")));
        registerObjectType("Condition", Collections.singleton(HTMLLikeParser.parseHTMLLikeParagraph(null, "Condition " +
                "defined within the game that might become true due to some effect, " +
                "usually used in conjunction with " +
                "<h navigate:" + DocumentationBuilder.getBuiltInObjectMethodPageId("os", "waitFor") + ">waitFor</h>, " +
                "<h navigate:" + DocumentationBuilder.getBuiltInObjectMethodPageId("os", "all") + ">all</h>, or " +
                "<h navigate:" + DocumentationBuilder.getBuiltInObjectMethodPageId("os", "any") + ">any</h> - methods" +
                " of built in " +
                "<h navigate:" + DocumentationBuilder.getBuiltInObjectPageId("os") + ">os</h> variable. ")));

        registerComputerDefinedVariable("console", "Contains functions that allow to manipulate Computer console.",
                null);
        registerComputerDefinedVariableFunction("console", "append", new AppendToConsoleFunction());
        registerComputerDefinedVariableFunction("console", "clear", new ClearConsoleFunction());
        registerComputerDefinedVariableFunction("console", "write", new WriteToConsoleFunction());

        registerComputerDefinedVariable("os", "Contains various functions that allow to do some common operations.",
                null);
        registerComputerDefinedVariableFunction("os", "parseFloat", new ParseFloatFunction());
        registerComputerDefinedVariableFunction("os", "parseInt", new ParseIntFunction());
        registerComputerDefinedVariableFunction("os", "typeOf", new TypeOfFunction());
        registerComputerDefinedVariableFunction("os", "waitFor", new WaitForFunction());
        registerComputerDefinedVariableFunction("os", "createSleepMs", new CreateSleepMsFunction());
        registerComputerDefinedVariableFunction("os", "createSleepTick", new CreateSleepTickFunction());
        registerComputerDefinedVariableFunction("os", "any", new AnyFunction());
        registerComputerDefinedVariableFunction("os", "all", new AllFunction());
        registerComputerDefinedVariableFunction("os", "format", new FormatFunction());

        registerComputerDefinedVariable("computer", "Contains functions that allow reading information about the " +
                "computer, as well as " +
                "bind modules for their use.", null);
        registerComputerDefinedVariableFunction("computer", "bindModule", new BindModuleFunction());
        registerComputerDefinedVariableFunction("computer", "bindModuleOfType", new BindFirstModuleOfTypeFunction());
        registerComputerDefinedVariableFunction("computer", "getModuleSlotCount", new GetModuleSlotCountFunction());
        registerComputerDefinedVariableFunction("computer", "getModuleType", new GetModuleTypeFunction());
    }

    @Override
    public void registerComputerModule(String type, ComputerModule computerModule, String description,
                                       Collection<ParagraphData> additionalParagraphs) {
        computerModuleRegistry.put(type, computerModule);
        String moduleName = computerModule.getModuleName();
        computerModulesByName.put(moduleName, computerModule);
        computerModuleDescriptions.put(moduleName, description);
        computerModuleAdditionalParagraphs.put(moduleName, additionalParagraphs);
    }

    @Override
    public ComputerModule getComputerModuleByType(String type) {
        return computerModuleRegistry.get(type);
    }

    @Override
    public void registerObjectType(String objectType, Collection<ParagraphData> documentation) {
        objectTypes.put(objectType, documentation);
    }

    @Override
    public void registerComputerDefinedVariable(String variable, String description,
                                                Collection<ParagraphData> additionalParagraphs) {
        objectDefinitions.put(variable, new MapObjectDefinition());
        objectDescriptions.put(variable, description);
        objectAdditionalParagraphs.put(variable, additionalParagraphs);
    }

    @Override
    public void registerComputerDefinedVariableFunction(String variable, String function,
                                                        DocumentedFunctionExecutable functionExecutable) {
        objectDefinitions.get(variable).addMember(function, functionExecutable);
    }

    @Override
    public void initializeContext(ComputerLanguageContext computerLanguageContext) {
        for (Map.Entry<String, Collection<ParagraphData>> objectTypeDocumentation : objectTypes.entrySet()) {
            computerLanguageContext.addObjectType(objectTypeDocumentation.getKey(), objectTypeDocumentation.getValue());
        }

        for (Map.Entry<String, MapObjectDefinition> objectDefinitionEntry : objectDefinitions.entrySet()) {
            String variable = objectDefinitionEntry.getKey();
            computerLanguageContext.addObject(variable, objectDefinitionEntry.getValue(),
                    objectDescriptions.get(variable), objectAdditionalParagraphs.get(variable));
        }

        for (ComputerModule computerModule : computerModulesByName.values()) {
            String moduleName = computerModule.getModuleName();

            computerLanguageContext.addComputerModule(computerModule, computerModuleDescriptions.get(moduleName),
                    computerModuleAdditionalParagraphs.get(moduleName));
        }
    }
}
