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

import com.google.common.collect.Multimap;
import com.google.common.collect.Ordering;
import com.google.common.collect.TreeMultimap;
import org.terasology.engine.rendering.nui.widgets.browser.data.DocumentData;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class DefaultDocumentationData implements DocumentationData, TableOfContents {
    private Set<PageData> rootPages = new TreeSet<>(new PageDataComparator());
    private Multimap<String, PageData> childPages = TreeMultimap.create(Ordering.natural(), new PageDataComparator());
    private Map<String, PageData> allPages = new HashMap<>();

    public void addEntry(String parent, PageData pageData) {
        addEntry(parent, pageData, true);
    }

    public void addEntry(String parent, PageData pageData, boolean addToToC) {
        if (addToToC) {
            if (parent != null) {
                childPages.put(parent, pageData);
            } else {
                rootPages.add(pageData);
            }
        }
        allPages.put(pageData.getPageId(), pageData);
    }

    @Override
    public DocumentationPageInfo getPageInfo(String pageId) {
        return allPages.get(pageId);
    }

    @Override
    public Collection<DocumentationPageInfo> getContents(String parentPageId) {
        if (parentPageId == null) {
            return Collections.unmodifiableCollection(rootPages);
        }
        Collection<PageData> children = childPages.get(parentPageId);
        if (children == null) {
            return null;
        }
        return Collections.unmodifiableCollection(children);
    }

    @Override
    public DocumentData getDocument(String pageId) {
        return allPages.get(pageId);
    }

    private static class PageDataComparator implements Comparator<PageData> {
        public int compare(PageData o1, PageData o2) {
            return o1.getDisplayableTitle().compareTo(o2.getDisplayableTitle());
        }
    }
}
