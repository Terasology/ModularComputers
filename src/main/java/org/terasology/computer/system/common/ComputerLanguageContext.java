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

import org.terasology.rendering.nui.widgets.browser.data.ParagraphData;
import org.terasology.computer.system.server.lang.ComputerModule;

import java.util.Collection;

public interface ComputerLanguageContext {
    void addObjectType(String objectType, Collection<ParagraphData> documentation);

    void addObject(String object, DocumentedObjectDefinition objectDefinition, String objectDescription,
                   Collection<ParagraphData> additionalParagraphs);

    void addComputerModule(ComputerModule computerModule, String description,
                           Collection<ParagraphData> additionalParagraphs);
}
