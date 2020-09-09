// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.computer.ui.documentation;

import org.terasology.engine.rendering.nui.widgets.browser.data.DocumentData;
import org.terasology.engine.rendering.nui.widgets.browser.data.ParagraphData;
import org.terasology.engine.rendering.nui.widgets.browser.ui.style.DocumentRenderStyle;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class PageData implements DocumentationPageInfo, DocumentData {
    private final String parentPageId;
    private final String pageId;
    private final String displayTitle;
    private final DocumentRenderStyle documentRenderStyle;
    private final List<ParagraphData> paragraphs = new LinkedList<>();

    public PageData(String pageId, String parentPageId, String displayTitle, DocumentRenderStyle documentRenderStyle) {
        this.pageId = pageId;
        this.parentPageId = parentPageId;
        this.displayTitle = displayTitle;
        this.documentRenderStyle = documentRenderStyle;
    }

    public void addParagraph(ParagraphData paragraphData) {
        paragraphs.add(paragraphData);
    }

    public void addParagraphs(Collection<ParagraphData> paragraphsToAdd) {
        if (paragraphsToAdd != null) {
            paragraphs.addAll(paragraphsToAdd);
        }
    }

    @Override
    public String getParentPageId() {
        return parentPageId;
    }

    @Override
    public String getPageId() {
        return pageId;
    }

    @Override
    public String getDisplayableTitle() {
        return displayTitle;
    }

    @Override
    public DocumentRenderStyle getDocumentRenderStyle() {
        return documentRenderStyle;
    }

    @Override
    public Collection<ParagraphData> getParagraphs() {
        return Collections.unmodifiableCollection(paragraphs);
    }
}
