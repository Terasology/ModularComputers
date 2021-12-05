// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.computer.system.common;

import org.terasology.computer.system.server.lang.ComputerModule;
import org.terasology.engine.rendering.nui.widgets.browser.data.ParagraphData;

import java.util.Collection;

public interface ComputerModuleRegistry {
    void registerComputerModule(String type, ComputerModule computerModule, String description,
                                Collection<ParagraphData> additionalParagraphs);

    ComputerModule getComputerModuleByType(String type);
}
