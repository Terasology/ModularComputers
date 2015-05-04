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

import org.terasology.browser.data.ParagraphData;
import org.terasology.browser.data.basic.HTMLLikeParser;

import java.util.Collection;
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
            return HTMLLikeParser.parseHTMLLike(null, simpleDocumentation);
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
