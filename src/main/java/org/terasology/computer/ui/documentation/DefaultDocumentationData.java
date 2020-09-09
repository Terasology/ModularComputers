// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
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
    private final Set<PageData> rootPages = new TreeSet<>(new PageDataComparator());
    private final Multimap<String, PageData> childPages = TreeMultimap.create(Ordering.natural(),
            new PageDataComparator());
    private final Map<String, PageData> allPages = new HashMap<>();

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
