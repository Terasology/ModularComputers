// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.computer.system.common;

import org.terasology.computer.system.server.lang.ComputerModule;
import org.terasology.engine.rendering.nui.widgets.browser.data.ParagraphData;

import java.util.Collection;

public interface ComputerLanguageContext {
    void addObjectType(String objectType, Collection<ParagraphData> documentation);

    void addObject(String object, DocumentedObjectDefinition objectDefinition, String objectDescription,
                   Collection<ParagraphData> additionalParagraphs);

    void addComputerModule(ComputerModule computerModule, String description,
                           Collection<ParagraphData> additionalParagraphs);
}
