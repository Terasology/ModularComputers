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
package org.terasology.computer.ui.documentation;

import org.terasology.engine.rendering.nui.widgets.browser.data.DocumentData;
import org.terasology.engine.rendering.nui.widgets.browser.data.ParagraphData;
import org.terasology.engine.rendering.nui.widgets.browser.ui.style.DocumentRenderStyle;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class PageData implements DocumentationPageInfo, DocumentData {
    private String parentPageId;
    private String pageId;
    private String displayTitle;
    private DocumentRenderStyle documentRenderStyle;
    private List<ParagraphData> paragraphs = new LinkedList<>();

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
