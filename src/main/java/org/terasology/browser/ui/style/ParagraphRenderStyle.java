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

public interface ParagraphRenderStyle extends TextRenderStyle {
    public default Integer getParagraphIndentTop(boolean firstParagraph) {
        return null;
    }

    public default Integer getParagraphIndentBottom(boolean lastParagraph) {
        return null;
    }

    public default Integer getParagraphIndentLeft() {
        return null;
    }

    public default Integer getParagraphIndentRight() {
        return null;
    }

    public default Integer getParagraphBackgroundIndentTop() {
        return null;
    }

    public default Integer getParagraphBackgroundIndentBottom() {
        return null;
    }

    public default Integer getParagraphBackgroundIndentLeft() {
        return null;
    }

    public default Integer getParagraphBackgroundIndentRight() {
        return null;
    }

    public default Color getParagraphBackground() {
        return null;
    }

    public default Integer getParagraphMinimumWidth() {
        return null;
    }
}
