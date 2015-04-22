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
    Map<String, Map<String, String>> functionDescriptions = new HashMap<>();
    Map<String, Map<String, Map<String, String>>> functionParametersDescriptions = new HashMap<>();
    Map<String, Map<String, String>> functionReturnDescriptions = new HashMap<>();

    private Map<String, ComputerModule> computerModuleRegistry = new HashMap<>();
    private Map<String, String> computerModuleDescriptions = new TreeMap<>();
    private Map<String, Map<String, String>> computerModuleFunctions = new HashMap<>();
    private Map<String, Map<String, Map<String, String>>> computerModuleFunctionParameters = new HashMap<>();
    private Map<String, Map<String, String>> computerModuleFunctionReturnDescriptions = new HashMap<>();

    @Override
    public void initialise() {
        registerComputerDefinedVariable("console", "Contains functions that allow to manipulate Computer console.");
        registerComputerDefinedVariableFunction("console", "append", new AppendToConsoleFunction(),
                "This method appends the specified text as a new line in Computer console.",
                new LinkedHashMap<String, String>() {{
                    put("text", "[String] Text to display in the Computer console.");
                }}, null);
        registerComputerDefinedVariableFunction("console", "clear", new ClearConsoleFunction(),
                "This method clears everything int the Computer console.", Collections.emptyMap(), null);
        registerComputerDefinedVariableFunction("console", "write", new WriteToConsoleFunction(),
                "This method writes the specified text at the specified place in Computer console",
                new LinkedHashMap<String, String>() {{
                    put("x", "[Number] X position of the text start in the console (column).");
                    put("y", "[Number] Y position of the text start in the console (row).");
                    put("text", "[String] Text to display at the specified position in the console.");
                }}, null);

        registerComputerDefinedVariable("os", "Contains various functions that allow to do some common operations.");
        registerComputerDefinedVariableFunction("os", "parseFloat", new ParseFloatFunction(),
                "Parses the specified text as a float number.",
                new LinkedHashMap<String, String>() {{
                    put("text", "[String] Text to parse as a float number.");
                }}, "[Number] The number it was able to parse.");
        registerComputerDefinedVariableFunction("os", "parseInt", new ParseIntFunction(),
                "Parses the specified text as an integer number.",
                new LinkedHashMap<String, String>() {{
                    put("text", "[String] Text to parse as an integer number.");
                }}, "[Number] The number it was able to parse.");
        registerComputerDefinedVariableFunction("os", "typeOf", new TypeOfFunction(),
                "Returns the type of the variable passed to it.",
                new LinkedHashMap<String, String>() {{
                    put("value", "[any] Value to return type for.");
                }}, "[String] Type of the variable.");
        registerComputerDefinedVariableFunction("os", "waitFor", new WaitForFunction(),
                "Waits for the specified condition to become true.",
                new LinkedHashMap<String, String>() {{
                    put("condition", "[Condition] Condition to wait to become true.");
                }}, "[any] The result returned by the condition upon meeting its requirement.");
        registerComputerDefinedVariableFunction("os", "createSleepMs", new CreateSleepMsFunction(),
                "Creates a condition that waits for the specified time in milliseconds.",
                new LinkedHashMap<String, String>() {{
                    put("time", "[Number] Number of milliseconds this condition should wait to become true.");
                }}, "[Condition] Condition that becomes true after specified number of milliseconds.");
        registerComputerDefinedVariableFunction("os", "createSleepTick", new CreateSleepTickFunction(),
                "Creates a condition that waits for the specified number of ticks.",
                new LinkedHashMap<String, String>() {{
                    put("ticks", "[Number] Number of ticks this condition should wait to become true.");
                }}, "[Condition] Condition that becomes true after specified number of ticks.");
        registerComputerDefinedVariableFunction("os", "any", new AnyFunction(),
                "Creates a condition that becomes true, when any the conditions passed becomes true.",
                new LinkedHashMap<String, String>() {{
                    put("conditions", "[Array of conditions] Conditions that this condition will wait for to become true.");
                }}, "[Condition] Condition that becomes true, when any of the passed conditions becomes true.\n" +
                        "In addition when this condition is <h navigate:" + DocumentationBuilder.getBuiltInObjectMethodPageId("os", "waitFor") + ">waitedFor</h> the waitFor for this " +
                        "condition will return an array containing two objects - index of the condition that became true, " +
                        "and the value returned by the condition.");
        registerComputerDefinedVariableFunction("os", "all", new AllFunction(),
                "Creates a condition that becomes true, when all the conditions passed become true.",
                new LinkedHashMap<String, String>() {{
                    put("conditions", "[Array of conditions] Conditions that this condition will wait for to become true.");
                }}, "[Condition] Condition that becomes true, when all of the passed conditions become true.\n" +
                        "In addition when this condition is <h navigate:" + DocumentationBuilder.getBuiltInObjectMethodPageId("os", "waitFor") + ">waitedFor</h> the waitFor for this " +
                        "condition will return an array containing all the objects returned by the conditions, in the " +
                        "order they appear in the original array passed as a parameter.");
        registerComputerDefinedVariableFunction("os", "format", new FormatFunction(),
                "Formats the specified number using the format passed as a parameter. This behaves exactly like DecimalFormat " +
                        "in Java language.",
                new LinkedHashMap<String, String>() {{
                    put("format", "[String] Format to use to output the number, as specified in DecimalFormat class in Java language");
                    put("number", "[Number] Number to format.");
                }}, "[String] Formatted number as specified by parameters.");

        registerComputerDefinedVariable("computer", "Contains functions that allow reading information about the computer, as well as " +
                "bind modules for their use.");
        registerComputerDefinedVariableFunction("computer", "bindModule", new BindModuleFunction(),
                "Binds the module specified in the slot number.",
                new LinkedHashMap<String, String>() {{
                    put("slot", "[Number] Slot number of a module to bind.");
                }}, "[Object] Binding to the module. This object exposes all the methods, as described in documentation for the module.");
        registerComputerDefinedVariableFunction("computer", "bindModuleOfType", new BindFirstModuleOfTypeFunction(),
                "Binds first module of the specified type in any of the slots.",
                new LinkedHashMap<String, String>() {{
                    put("type", "[String] Type of the module to bind.");
                }}, "[Object] Binding to the module. This object exposes all the methods, as described in documentation for the module. " +
                        "If the module is not found in this computer, this function returns null.");
        registerComputerDefinedVariableFunction("computer", "getModuleSlotCount", new GetModuleSlotCountFunction(),
                "Returns number of module slots in this computer.",
                new LinkedHashMap<>(), "[Number] Number of module slots in this computer.");
        registerComputerDefinedVariableFunction("computer", "getModuleType", new GetModuleTypeFunction(),
                "Returns the module type at the specified slot.",
                new LinkedHashMap<String, String>() {{
                    put("slot", "Slot to check for module type.");
                }}, "[String] Module type at the slot specified, or null if no module at that slot is present.");
    }

    @Override
    public void registerComputerModule(String type, ComputerModule computerModule, String description, Map<String, String> methodDescriptions,
                                       Map<String, Map<String, String>> methodParametersDescriptions, Map<String, String> returnValuesDescriptions) {
        computerModuleRegistry.put(type, computerModule);
        String moduleName = computerModule.getModuleName();
        computerModuleDescriptions.put(moduleName, description);
        computerModuleFunctions.put(moduleName, new TreeMap<>(methodDescriptions));
        computerModuleFunctionParameters.put(moduleName, methodParametersDescriptions);
        computerModuleFunctionReturnDescriptions.put(moduleName, returnValuesDescriptions);
    }

    @Override
    public ComputerModule getComputerModuleByType(String type) {
        return computerModuleRegistry.get(type);
    }

    @Override
    public void registerComputerDefinedVariable(String variable, String description) {
        objectDefinitions.put(variable, new MapObjectDefinition());
        objectDescriptions.put(variable, description);

        functionDescriptions.put(variable, new TreeMap<>());
        functionParametersDescriptions.put(variable, new HashMap<>());
        functionReturnDescriptions.put(variable, new HashMap<>());
    }

    @Override
    public void registerComputerDefinedVariableFunction(String variable, String function, FunctionExecutable functionExecutable, String description, Map<String, String> parametersDescription, String returnValueDescription) {
        objectDefinitions.get(variable).addMember(function, functionExecutable);

        functionDescriptions.get(variable).put(function, description);
        functionParametersDescriptions.get(variable).put(function, parametersDescription);
        functionReturnDescriptions.get(variable).put(function, returnValueDescription);
    }

    @Override
    public void initializeContext(ComputerLanguageContext computerLanguageContext) {
        for (Map.Entry<String, MapObjectDefinition> objectDefinitionEntry : objectDefinitions.entrySet()) {
            String variable = objectDefinitionEntry.getKey();
            computerLanguageContext.addObject(variable, objectDefinitionEntry.getValue(),
                    objectDescriptions.get(variable), functionDescriptions.get(variable),
                    functionParametersDescriptions.get(variable), functionReturnDescriptions.get(variable));
        }

        for (ComputerModule computerModule : computerModuleRegistry.values()) {
            String moduleName = computerModule.getModuleName();
            computerLanguageContext.addComputerModule(computerModule, computerModuleDescriptions.get(moduleName),
                    computerModuleFunctions.get(moduleName), computerModuleFunctionParameters.get(moduleName),
                    computerModuleFunctionReturnDescriptions.get(moduleName));
        }
    }
}
