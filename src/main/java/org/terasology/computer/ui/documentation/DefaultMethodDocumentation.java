// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.computer.ui.documentation;

import org.terasology.engine.rendering.nui.widgets.browser.data.ParagraphData;
import org.terasology.engine.rendering.nui.widgets.browser.data.basic.HTMLLikeParser;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class DefaultMethodDocumentation implements MethodDocumentation {
    private String simpleDocumentation;
    private String returnType;
    private String returnDocumentation;
    private Collection<ParagraphData> pageDocumentation;
    private Map<String, String> parameterTypes = new HashMap<>();
    private Map<String, String> parameterDocumentation = new HashMap<>();
    private List<Collection<ParagraphData>> examples = new LinkedList<>();

    public DefaultMethodDocumentation(String simpleDocumentation) {
        this(simpleDocumentation, null, null);
    }

    public DefaultMethodDocumentation(String simpleDocumentation, String returnType, String returnDocumentation) {
        this.simpleDocumentation = simpleDocumentation;
        this.returnType = returnType;
        this.returnDocumentation = returnDocumentation;
    }

    @Override
    public Collection<ParagraphData> getPageDocumentation() {
        if (pageDocumentation == null) {
            return Collections.singleton(HTMLLikeParser.parseHTMLLikeParagraph(null, simpleDocumentation));
        }

        return pageDocumentation;
    }

    @Override
    public String getHTMLLikeReturnDocumentation() {
        return returnDocumentation;
    }

    @Override
    public String getHTMLLikeSimpleDocumentation() {
        return simpleDocumentation;
    }

    @Override
    public String getReturnType() {
        return returnType;
    }

    @Override
    public String getParameterType(String parameterName) {
        return parameterTypes.get(parameterName);
    }

    @Override
    public String getHTMLLikeParameterDocumentation(String parameterName) {
        return parameterDocumentation.get(parameterName);
    }

    @Override
    public Collection<Collection<ParagraphData>> getExamples() {
        return examples;
    }

    public void setPageDocumentation(Collection<ParagraphData> pageDocumentation) {
        this.pageDocumentation = pageDocumentation;
    }

    public void addParameterDocumentation(String parameterName, String type, String documentation) {
        parameterTypes.put(parameterName, type);
        parameterDocumentation.put(parameterName, documentation);
    }

    public void addExample(Collection<ParagraphData> example) {
        examples.add(example);
    }
}
