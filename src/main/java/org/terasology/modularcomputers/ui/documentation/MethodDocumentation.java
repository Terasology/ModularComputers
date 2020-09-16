// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.modularcomputers.ui.documentation;

import org.terasology.engine.rendering.nui.widgets.browser.data.ParagraphData;
import org.terasology.engine.rendering.nui.widgets.browser.data.basic.HTMLLikeParser;

import java.util.Collection;
import java.util.Collections;

public interface MethodDocumentation {
    String getHTMLLikeSimpleDocumentation();

    default Collection<ParagraphData> getPageDocumentation() {
        return Collections.singleton(HTMLLikeParser.parseHTMLLikeParagraph(null, getHTMLLikeSimpleDocumentation()));
    }

    String getReturnType();

    String getParameterType(String parameterName);

    String getHTMLLikeParameterDocumentation(String parameterName);

    default String getHTMLLikeReturnDocumentation() {
        return null;
    }

    Collection<Collection<ParagraphData>> getExamples();
}
