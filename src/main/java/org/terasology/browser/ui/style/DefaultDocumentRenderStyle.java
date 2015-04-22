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

import org.terasology.rendering.assets.font.Font;
import org.terasology.rendering.nui.Canvas;
import org.terasology.rendering.nui.Color;

public class DefaultDocumentRenderStyle implements DocumentRenderStyle {
    private Canvas canvas;

    public DefaultDocumentRenderStyle(Canvas canvas) {
        this.canvas = canvas;
    }

    @Override
    public Integer getIndentAbove(boolean firstParagraph) {
        return 0;
    }

    @Override
    public Integer getIndentBelow(boolean lastParagraph) {
        return 0;
    }

    @Override
    public Integer getIndentLeft() {
        return 0;
    }

    @Override
    public Integer getIndentRight() {
        return 0;
    }

    @Override
    public Font getFont(boolean hyperlink) {
        return canvas.getCurrentStyle().getFont();
    }

    @Override
    public Color getColor(boolean hyperlink) {
        if (hyperlink) {
            return Color.BLUE;
        } else {
            return canvas.getCurrentStyle().getTextColor();
        }
    }

    @Override
    public Color getBackgroundColor() {
        return null;
    }

    @Override
    public Color getParagraphBackground() {
        return null;
    }
}
