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
package org.terasology.browser.ui.style.base;

import org.terasology.browser.ui.style.ParagraphRenderStyle;
import org.terasology.rendering.nui.Color;

public class BaseParagraphRenderStyle extends BaseTextRenderStyle implements ParagraphRenderStyle {
    @Override
    public Integer getParagraphIndentTop(boolean firstParagraph) {
        return null;
    }

    @Override
    public Integer getParagraphIndentBottom(boolean lastParagraph) {
        return null;
    }

    @Override
    public Integer getParagraphIndentLeft() {
        return null;
    }

    @Override
    public Integer getParagraphIndentRight() {
        return null;
    }

    @Override
    public Integer getParagraphBackgroundIndentTop() {
        return null;
    }

    @Override
    public Integer getParagraphBackgroundIndentBottom() {
        return null;
    }

    @Override
    public Integer getParagraphBackgroundIndentLeft() {
        return null;
    }

    @Override
    public Integer getParagraphBackgroundIndentRight() {
        return null;
    }

    @Override
    public Color getParagraphBackground() {
        return null;
    }
}
