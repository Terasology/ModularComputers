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
@Share(value = {ComputerDefinedVariablesRegistry.class, ComputerLanguageContextInitializer.class})
public class ComputerLanguageCommonSystem extends BaseComponentSystem implements ComputerDefinedVariablesRegistry,
        ComputerLanguageContextInitializer
{
    private Map<String, MapObjectDefinition> objectDefinitions = new TreeMap<>();
    private Map<String, Collection<ParagraphData>> objectDescriptions = new HashMap<>();
    Map<String, Map<String, Collection<ParagraphData>>> functionDescriptions = new HashMap<>();
    Map<String, Map<String, Map<String, Collection<ParagraphData>>>> functionParametersDescriptions = new HashMap<>();
    Map<String, Map<String, Collection<ParagraphData>>> functionReturnDescriptions = new HashMap<>();

    @Override
    public void initialise() {
        registerComputerDefinedVariable("console", Collections.emptyList());
        registerComputerDefinedVariableFunction("console", "append", new AppendToConsoleFunction(), Collections.emptyList(), Collections.emptyMap(), Collections.emptyList());
        registerComputerDefinedVariableFunction("console", "clear", new ClearConsoleFunction(), Collections.emptyList(), Collections.emptyMap(), Collections.emptyList());
        registerComputerDefinedVariableFunction("console", "write", new WriteToConsoleFunction(), Collections.emptyList(), Collections.emptyMap(), Collections.emptyList());

        registerComputerDefinedVariable("os", Collections.emptyList());
        registerComputerDefinedVariableFunction("os", "parseFloat", new ParseFloatFunction(), Collections.emptyList(), Collections.emptyMap(), Collections.emptyList());
        registerComputerDefinedVariableFunction("os", "parseInt", new ParseIntFunction(), Collections.emptyList(), Collections.emptyMap(), Collections.emptyList());
        registerComputerDefinedVariableFunction("os", "typeOf", new TypeOfFunction(), Collections.emptyList(), Collections.emptyMap(), Collections.emptyList());
        registerComputerDefinedVariableFunction("os", "waitFor", new WaitForFunction(), Collections.emptyList(), Collections.emptyMap(), Collections.emptyList());
        registerComputerDefinedVariableFunction("os", "createSleepMs", new CreateSleepMsFunction(), Collections.emptyList(), Collections.emptyMap(), Collections.emptyList());
        registerComputerDefinedVariableFunction("os", "createSleepTick", new CreateSleepTickFunction(), Collections.emptyList(), Collections.emptyMap(), Collections.emptyList());
        registerComputerDefinedVariableFunction("os", "any", new AnyFunction(), Collections.emptyList(), Collections.emptyMap(), Collections.emptyList());
        registerComputerDefinedVariableFunction("os", "all", new AllFunction(), Collections.emptyList(), Collections.emptyMap(), Collections.emptyList());
        registerComputerDefinedVariableFunction("os", "format", new FormatFunction(), Collections.emptyList(), Collections.emptyMap(), Collections.emptyList());

        registerComputerDefinedVariable("computer", Collections.emptyList());
        registerComputerDefinedVariableFunction("computer", "bindModule", new BindModuleFunction(), Collections.emptyList(), Collections.emptyMap(), Collections.emptyList());
        registerComputerDefinedVariableFunction("computer", "bindModuleOfType", new BindFirstModuleOfTypeFunction(), Collections.emptyList(), Collections.emptyMap(), Collections.emptyList());
        registerComputerDefinedVariableFunction("computer", "getModuleSlotCount", new GetModuleSlotCountFunction(), Collections.emptyList(), Collections.emptyMap(), Collections.emptyList());
        registerComputerDefinedVariableFunction("computer", "getModuleType", new GetModuleTypeFunction(), Collections.emptyList(), Collections.emptyMap(), Collections.emptyList());
    }

    @Override
    public void registerComputerDefinedVariable(String variable, Collection<ParagraphData> description) {
        objectDefinitions.put(variable, new MapObjectDefinition());
        objectDescriptions.put(variable, description);

        functionDescriptions.put(variable, new TreeMap<>());
        functionParametersDescriptions.put(variable, new HashMap<>());
        functionReturnDescriptions.put(variable, new HashMap<>());
    }

    @Override
    public void registerComputerDefinedVariableFunction(String variable, String function, FunctionExecutable functionExecutable, Collection<ParagraphData> description, Map<String, Collection<ParagraphData>> parametersDescription, Collection<ParagraphData> returnValueDescription) {
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
    }
}
