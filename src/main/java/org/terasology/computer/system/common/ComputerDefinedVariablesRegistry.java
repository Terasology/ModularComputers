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

import java.util.Collection;
import java.util.Map;

public interface ComputerDefinedVariablesRegistry {
    public void registerComputerDefinedVariable(String variable, String description, Collection<ParagraphData> additionalParagraphs);

    public void registerComputerDefinedVariableFunction(String variable, String function, FunctionExecutable terasologyFunctionExecutable,
                                                        String description, Map<String, String> parametersDescription,
                                                        String returnValueDescription, Collection<ParagraphData> additionalParagraphs);
}
