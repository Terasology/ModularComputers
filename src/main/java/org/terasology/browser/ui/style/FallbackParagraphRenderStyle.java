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
package org.terasology.browser.ui.style;

import org.terasology.rendering.nui.Color;

public class FallbackParagraphRenderStyle extends FallbackTextRenderStyle implements ParagraphRenderStyle {
    private ParagraphRenderStyle style;
    private ParagraphRenderStyle fallback;

    public FallbackParagraphRenderStyle(ParagraphRenderStyle style, ParagraphRenderStyle fallback) {
        super(style, fallback);
        this.style = style;
        this.fallback = fallback;
    }

    @Override
    public Integer getIndentAbove(boolean firstParagraph) {
        Integer indentAbove = style.getIndentAbove(firstParagraph);
        if (indentAbove == null) {
            indentAbove = fallback.getIndentAbove(firstParagraph);
        }
        return indentAbove;
    }

    @Override
    public Integer getIndentBelow(boolean lastParagraph) {
        Integer indentBelow = style.getIndentBelow(lastParagraph);
        if (indentBelow == null) {
            indentBelow = fallback.getIndentBelow(lastParagraph);
        }
        return indentBelow;
    }

    @Override
    public Integer getIndentLeft() {
        Integer indentLeft = style.getIndentLeft();
        if (indentLeft == null) {
            indentLeft = fallback.getIndentLeft();
        }
        return indentLeft;
    }

    @Override
    public Integer getIndentRight() {
        Integer indentRight = style.getIndentRight();
        if (indentRight == null) {
            indentRight = fallback.getIndentRight();
        }
        return indentRight;
    }

    @Override
    public Color getParagraphBackground() {
        Color paragraphBackground = style.getParagraphBackground();
        if (paragraphBackground == null) {
            paragraphBackground = fallback.getParagraphBackground();
        }
        return paragraphBackground;
    }
}
