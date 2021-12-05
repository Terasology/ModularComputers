// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.computer.system.common;

import org.terasology.engine.rendering.nui.widgets.browser.data.ParagraphData;

import java.util.Collection;

public interface ComputerLanguageRegistry {
    void registerObjectType(String objectType, Collection<ParagraphData> documentation);

    void registerComputerDefinedVariable(String variable, String description, Collection<ParagraphData> additionalParagraphs);

    void registerComputerDefinedVariableFunction(String variable, String function, DocumentedFunctionExecutable documentedFunctionExecutable);
}
