// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.computer.ui.documentation;

import org.terasology.engine.rendering.nui.widgets.browser.data.DocumentData;

public interface DocumentationData {
    DocumentData getDocument(String pageId);
}
