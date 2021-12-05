// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.computer.ui.documentation;

import java.util.Collection;

public interface TableOfContents {
    DocumentationPageInfo getPageInfo(String pageId);
    Collection<DocumentationPageInfo> getContents(
            String parentPageId);
}
