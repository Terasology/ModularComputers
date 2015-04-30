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
package org.terasology.computer.system.common;

import com.gempukku.lang.FunctionExecutable;
import org.terasology.browser.data.ParagraphData;
import org.terasology.computer.system.server.lang.ComputerModule;
import org.terasology.computer.system.server.lang.computer.BindFirstModuleOfTypeFunction;
import org.terasology.computer.system.server.lang.computer.BindModuleFunction;
import org.terasology.computer.system.server.lang.computer.GetModuleSlotCountFunction;
import org.terasology.computer.system.server.lang.computer.GetModuleTypeFunction;
import org.terasology.computer.system.server.lang.console.AppendToConsoleFunction;
import org.terasology.computer.system.server.lang.console.ClearConsoleFunction;
import org.terasology.computer.system.server.lang.console.WriteToConsoleFunction;
import org.terasology.computer.system.server.lang.os.AllFunction;
import org.terasology.computer.system.server.lang.os.AnyFunction;
import org.terasology.computer.system.server.lang.os.CreateSleepMsFunction;
import org.terasology.computer.system.server.lang.os.CreateSleepTickFunction;
import org.terasology.computer.system.server.lang.os.FormatFunction;
import org.terasology.computer.system.server.lang.os.ParseFloatFunction;
import org.terasology.computer.system.server.lang.os.ParseIntFunction;
import org.terasology.computer.system.server.lang.os.TypeOfFunction;
import org.terasology.computer.system.server.lang.os.WaitForFunction;
import org.terasology.computer.ui.documentation.DocumentationBuilder;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.registry.Share;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

@RegisterSystem(RegisterMode.ALWAYS)
@Share(value = {ComputerDefinedVariablesRegistry.class, ComputerModuleRegistry.class, ComputerLanguageContextInitializer.class})
public class ComputerLanguageCommonSystem extends BaseComponentSystem implements ComputerDefinedVariablesRegistry,
        ComputerModuleRegistry, ComputerLanguageContextInitializer {

    private Map<String, MapObjectDefinition> objectDefinitions = new TreeMap<>();
    private Map<String, String> objectDescriptions = new HashMap<>();
    private Map<String, Collection<ParagraphData>> objectAdditionalParagraphs = new HashMap<>();
    private Map<String, Map<String, String>> functionDescriptions = new HashMap<>();
    private Map<String, Map<String, Map<String, String>>> functionParametersDescriptions = new HashMap<>();
    private Map<String, Map<String, String>> functionReturnDescriptions = new HashMap<>();
    private Map<String, Map<String, Collection<ParagraphData>>> functionAdditionalParagraphs = new HashMap<>();

    private Map<String, ComputerModule> computerModuleRegistry = new HashMap<>();
    private Map<String, String> computerModuleDescriptions = new TreeMap<>();
    private Map<String, Collection<ParagraphData>> computerModuleAdditionalParagraphs = new HashMap<>();
    private Map<String, Map<String, String>> computerModuleFunctions = new HashMap<>();
    private Map<String, Map<String, Map<String, String>>> computerModuleFunctionParameters = new HashMap<>();
    private Map<String, Map<String, String>> computerModuleFunctionReturnDescriptions = new HashMap<>();
    private Map<String, Map<String, Collection<ParagraphData>>> computerModuleFunctionAdditionalParagraphs = new HashMap<>();

    @Override
    public void initialise() {
        registerComputerDefinedVariable("console", "Contains functions that allow to manipulate Computer console.", null);
        registerComputerDefinedVariableFunction("console", "append", new AppendToConsoleFunction(),
                "This method appends the specified text as a new line in Computer console.",
                new LinkedHashMap<String, String>() {
                    {
                        put("text", "[String] Text to display in the Computer console.");
                    }
                }, null, DocumentationBuilder.createExampleParagraphs(
                        "This example program appends specified text to Computer Console.",
                        "console.append(\"Hello World!\");"
                ));
        registerComputerDefinedVariableFunction("console", "clear", new ClearConsoleFunction(),
                "This method clears everything int the Computer console.", Collections.emptyMap(), null,
                DocumentationBuilder.createExampleParagraphs(
                        "This example program clears the screen of the Computer Console. Please note, it first appends " +
                                "a line of text to it, to show it actually works.",
                        "console.append(\"Line of text you won't see.\");\n" +
                                "console.clear();"
                ));
        registerComputerDefinedVariableFunction("console", "write", new WriteToConsoleFunction(),
                "This method writes the specified text at the specified place in Computer console",
                new LinkedHashMap<String, String>() {
                    {
                        put("x", "[Number] X position of the text start in the console (column).");
                        put("y", "[Number] Y position of the text start in the console (row).");
                        put("text", "[String] Text to display at the specified position in the console.");
                    }
                }, null, DocumentationBuilder.createExampleParagraphs(
                        "This example program writes the specified text at the specified position in console.",
                        "console.write(0, 5, \"This text is written on 6th line.\");"
                ));

        registerComputerDefinedVariable("os", "Contains various functions that allow to do some common operations.", null);
        registerComputerDefinedVariableFunction("os", "parseFloat", new ParseFloatFunction(),
                "Parses the specified text as a float number.",
                new LinkedHashMap<String, String>() {
                    {
                        put("text", "[String] Text to parse as a float number.");
                    }
                }, "[Number] The number it was able to parse.",
                DocumentationBuilder.createExampleParagraphs(
                        "This example program parses a specified text into a variable of type Number. Please note " +
                                "the output has the \".0\" appended at the end as any whole number has, when printed " +
                                "on the screen.",
                        "console.append(\"\"+os.parseFloat(\"123\"));"
                ));
        registerComputerDefinedVariableFunction("os", "parseInt", new ParseIntFunction(),
                "Parses the specified text as an integer number.",
                new LinkedHashMap<String, String>() { {
                    put("text", "[String] Text to parse as an integer number.");
                }}, "[Number] The number it was able to parse.",
                DocumentationBuilder.createExampleParagraphs(
                        "This example program parses a specified text into a variable of type Number. It allows only " +
                                "to parse whole numbers. Please note the output has the \".0\" appended at the end as " +
                                "any whole number has, when printed on the screen.",
                        "console.append(\"\"+os.parseInt(\"123\"));"
                ));
        registerComputerDefinedVariableFunction("os", "typeOf", new TypeOfFunction(),
                "Returns the type of the variable passed to it.",
                new LinkedHashMap<String, String>() {
                    {
                        put("value", "[any] Value to return type for.");
                    }
                }, "[String] Type of the variable.",
                DocumentationBuilder.createExampleParagraphs(
                        "This example program prints the type of a couple of different variables.",
                        "console.append(os.typeOf(\"text\"));\n" +
                                "console.append(os.typeOf(123));\n" +
                                "console.append(os.typeOf(true));\n" +
                                "console.append(os.typeOf(console));\n" +
                                "console.append(os.typeOf(null));"
                ));
        registerComputerDefinedVariableFunction("os", "waitFor", new WaitForFunction(),
                "Waits for the specified condition to become true.",
                new LinkedHashMap<String, String>() {
                    {
                        put("condition", "[Condition] Condition to wait to become true.");
                    }
                }, "[any] The result returned by the condition upon meeting its requirement.",
                DocumentationBuilder.createExampleParagraphs(
                        "This example creates a condition that waits for the specified number of milliseconds, waits " +
                                "for it and then prints out text to console.",
                        "var sleepCondition = os.createSleepMs(3000);\n" +
                                "os.waitFor(sleepCondition);\n" +
                                "console.append(\"This text is printed after 3 seconds have passed.\");"
                ));
        registerComputerDefinedVariableFunction("os", "createSleepMs", new CreateSleepMsFunction(),
                "Creates a condition that waits for the specified time in milliseconds.",
                new LinkedHashMap<String, String>() {
                    {
                        put("time", "[Number] Number of milliseconds this condition should wait to become true.");
                    }
                }, "[Condition] Condition that becomes true after specified number of milliseconds.",
                DocumentationBuilder.createExampleParagraphs(
                        "This example creates a condition that waits for the specified number of milliseconds, waits " +
                                "for it and then prints out text to console.",
                        "var sleepCondition = os.createSleepMs(3000);\n" +
                                "os.waitFor(sleepCondition);\n" +
                                "console.append(\"This text is printed after 3 seconds have passed.\");"
                ));
        registerComputerDefinedVariableFunction("os", "createSleepTick", new CreateSleepTickFunction(),
                "Creates a condition that waits for the specified number of ticks.",
                new LinkedHashMap<String, String>() {
                    {
                        put("ticks", "[Number] Number of ticks this condition should wait to become true.");
                    }
                }, "[Condition] Condition that becomes true after specified number of ticks.",
                DocumentationBuilder.createExampleParagraphs(
                        "This example creates a condition that waits for the specified number of ticks, waits " +
                                "for it and then prints out text to console.",
                        "var sleepCondition = os.createSleepTick(10);\n" +
                                "os.waitFor(sleepCondition);\n" +
                                "console.append(\"This text is printed after 10 ticks have passed.\");"
                ));
        registerComputerDefinedVariableFunction("os", "any", new AnyFunction(),
                "Creates a condition that becomes true, when any the conditions passed becomes true.",
                new LinkedHashMap<String, String>() {
                    {
                        put("conditions", "[Array of conditions] Conditions that this condition will wait for to become true.");
                    }
                }, "[Condition] Condition that becomes true, when any of the passed conditions becomes true.\n" +
                        "In addition when this condition is <h navigate:" + DocumentationBuilder.getBuiltInObjectMethodPageId("os", "waitFor") +
                        ">waitedFor</h> the waitFor for this " +
                        "condition will return an array containing two objects - index of the condition that became true, " +
                        "and the value returned by the condition.",
                DocumentationBuilder.createExampleParagraphs(
                        "This example creates two conditions, one waiting for 3 seconds, the other for 5 seconds, waits " +
                                "for ANY of them and prints out text to console.",
                        "var sleepCondition1 = os.createSleepMs(3000);\n" +
                                "var sleepCondition2 = os.createSleepMs(5000);\n" +
                                "var sleepAny = os.any([sleepCondition1, sleepCondition2]);\n" +
                                "os.waitFor(sleepAny);\n" +
                                "console.append(\"This text is printed after 3 seconds have passed.\");"
                ));
        registerComputerDefinedVariableFunction("os", "all", new AllFunction(),
                "Creates a condition that becomes true, when all the conditions passed become true.",
                new LinkedHashMap<String, String>() {
                    {
                        put("conditions", "[Array of conditions] Conditions that this condition will wait for to become true.");
                    }
                }, "[Condition] Condition that becomes true, when all of the passed conditions become true.\n" +
                        "In addition when this condition is <h navigate:" + DocumentationBuilder.getBuiltInObjectMethodPageId("os", "waitFor") +
                        ">waitedFor</h> the waitFor for this " +
                        "condition will return an array containing all the objects returned by the conditions, in the " +
                        "order they appear in the original array passed as a parameter.",
                DocumentationBuilder.createExampleParagraphs(
                        "This example creates two conditions, one waiting for 3 seconds, the other for 5 seconds, waits " +
                                "for ALL of them and prints out text to console.",
                        "var sleepCondition1 = os.createSleepMs(3000);\n" +
                                "var sleepCondition2 = os.createSleepMs(5000);\n" +
                                "var sleepAny = os.all([sleepCondition1, sleepCondition2]);\n" +
                                "os.waitFor(sleepAny);\n" +
                                "console.append(\"This text is printed after 5 seconds have passed.\");"
                ));
        registerComputerDefinedVariableFunction("os", "format", new FormatFunction(),
                "Formats the specified number using the format passed as a parameter. This behaves exactly like DecimalFormat " +
                        "in Java language.",
                new LinkedHashMap<String, String>() {
                    {
                        put("format", "[String] Format to use to output the number, as specified in DecimalFormat class in Java language");
                        put("number", "[Number] Number to format.");
                    }
                }, "[String] Formatted number as specified by parameters.",
                DocumentationBuilder.createExampleParagraphs(
                        "This example prints out a floating-point number in a specified format.",
                        "var oneThird = 1/3;\n" +
                                "console.append(os.format(\"0.00\", oneThird));"
                ));

        registerComputerDefinedVariable("computer", "Contains functions that allow reading information about the computer, as well as " +
                "bind modules for their use.", null);
        registerComputerDefinedVariableFunction("computer", "bindModule", new BindModuleFunction(),
                "Binds the module specified in the slot number.",
                new LinkedHashMap<String, String>() {
                    {
                        put("slot", "[Number] Slot number of a module to bind.");
                    }
                }, "[Object] Binding to the module. This object exposes all the methods, as described in documentation for the module.",
                DocumentationBuilder.createExampleParagraphs(
                        "This example binds module in the first slot and executes \"move\" method on it with \"up\" parameter. " +
                                "In order for successful execution - please place \"Mobility\" module in the first slot of the computer.",
                        "var moduleBinding = computer.bindModule(0);\n" +
                                "moduleBinding.move(\"up\");"
                ));
        registerComputerDefinedVariableFunction("computer", "bindModuleOfType", new BindFirstModuleOfTypeFunction(),
                "Binds first module of the specified type in any of the slots.",
                new LinkedHashMap<String, String>() {
                    {
                        put("type", "[String] Type of the module to bind.");
                    }
                }, "[Object] Binding to the module. This object exposes all the methods, as described in documentation for the module. " +
                        "If the module is not found in this computer, this function returns null.",
                DocumentationBuilder.createExampleParagraphs(
                        "This example binds first module of the specified type that it finds in computer's module slots to the variable " +
                                "and then executes \"move\" method on it with \"up\" parameter. " +
                                "In order for successful execution - please place \"Mobility\" module in any slot of the computer.",
                        "var moduleBinding = computer.bindModuleOfType(\"Mobility\");\n" +
                                "moduleBinding.move(\"up\");"
                ));
        registerComputerDefinedVariableFunction("computer", "getModuleSlotCount", new GetModuleSlotCountFunction(),
                "Returns number of module slots in this computer.",
                new LinkedHashMap<>(), "[Number] Number of module slots in this computer.",
                DocumentationBuilder.createExampleParagraphs(
                        "This example prints out the number of module slots this computer has.",
                        "console.append(\"This computer has \" + computer.getModuleSlotCount() + \" module slots.\");"
                ));
        registerComputerDefinedVariableFunction("computer", "getModuleType", new GetModuleTypeFunction(),
                "Returns the module type at the specified slot.",
                new LinkedHashMap<String, String>() {
                    {
                        put("slot", "Slot to check for module type.");
                    }
                }, "[String] Module type at the slot specified, or null if no module at that slot is present.",
                DocumentationBuilder.createExampleParagraphs(
                        "This example iterates over all module slots this computer has and prints out the type of the module " +
                                "in that slot.",
                        "var moduleSlotCount = computer.getModuleSlotCount();\n" +
                                "for (var i=0; i < moduleSlotCount; i++) {\n" +
                                "  var moduleType = computer.getModuleType(i);\n" +
                                "  if (moduleType == null)\n" +
                                "    console.append(\"Slot \"+(i+1)+\" has no module.\");\n" +
                                "  else\n" +
                                "    console.append(\"Slot \"+(i+1)+\" has module of type - \" + moduleType);\n" +
                                "}"
                ));
    }

    @Override
    public void registerComputerModule(String type, ComputerModule computerModule, String description, Collection<ParagraphData> additionalParagraphs,
                                       Map<String, String> methodDescriptions, Map<String, Map<String, String>> methodParametersDescriptions,
                                       Map<String, String> returnValuesDescriptions, Map<String, Collection<ParagraphData>> additionalMethodParagraphs) {
        computerModuleRegistry.put(type, computerModule);
        String moduleName = computerModule.getModuleName();
        computerModuleDescriptions.put(moduleName, description);
        computerModuleAdditionalParagraphs.put(moduleName, additionalParagraphs);
        computerModuleFunctions.put(moduleName, new TreeMap<>(methodDescriptions));
        computerModuleFunctionParameters.put(moduleName, methodParametersDescriptions);
        computerModuleFunctionReturnDescriptions.put(moduleName, returnValuesDescriptions);
        computerModuleFunctionAdditionalParagraphs.put(moduleName, (additionalMethodParagraphs != null) ? additionalMethodParagraphs : Collections.emptyMap());
    }

    @Override
    public ComputerModule getComputerModuleByType(String type) {
        return computerModuleRegistry.get(type);
    }

    @Override
    public void registerComputerDefinedVariable(String variable, String description, Collection<ParagraphData> additionalParagraphs) {
        objectDefinitions.put(variable, new MapObjectDefinition());
        objectDescriptions.put(variable, description);
        objectAdditionalParagraphs.put(variable, additionalParagraphs);

        functionDescriptions.put(variable, new TreeMap<>());
        functionParametersDescriptions.put(variable, new HashMap<>());
        functionReturnDescriptions.put(variable, new HashMap<>());
        functionAdditionalParagraphs.put(variable, new HashMap<>());
    }

    @Override
    public void registerComputerDefinedVariableFunction(String variable, String function, FunctionExecutable functionExecutable, String description,
                                                        Map<String, String> parametersDescription, String returnValueDescription,
                                                        Collection<ParagraphData> additionalParagraphs) {
        objectDefinitions.get(variable).addMember(function, functionExecutable);

        functionDescriptions.get(variable).put(function, description);
        functionParametersDescriptions.get(variable).put(function, parametersDescription);
        functionReturnDescriptions.get(variable).put(function, returnValueDescription);
        functionAdditionalParagraphs.get(variable).put(function, additionalParagraphs);
    }

    @Override
    public void initializeContext(ComputerLanguageContext computerLanguageContext) {
        for (Map.Entry<String, MapObjectDefinition> objectDefinitionEntry : objectDefinitions.entrySet()) {
            String variable = objectDefinitionEntry.getKey();
            computerLanguageContext.addObject(variable, objectDefinitionEntry.getValue(),
                    objectDescriptions.get(variable), objectAdditionalParagraphs.get(variable),
                    functionDescriptions.get(variable), functionParametersDescriptions.get(variable), functionReturnDescriptions.get(variable),
                    functionAdditionalParagraphs.get(variable));
        }

        for (ComputerModule computerModule : computerModuleRegistry.values()) {
            String moduleName = computerModule.getModuleName();
            computerLanguageContext.addComputerModule(computerModule, computerModuleDescriptions.get(moduleName),
                    computerModuleAdditionalParagraphs.get(moduleName), computerModuleFunctions.get(moduleName),
                    computerModuleFunctionParameters.get(moduleName), computerModuleFunctionReturnDescriptions.get(moduleName),
                    computerModuleFunctionAdditionalParagraphs.get(moduleName));
        }
    }
}
