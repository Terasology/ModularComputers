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
package org.terasology.browser.data.basic;

import org.terasology.browser.data.basic.HyperlinkParagraphData;
import org.terasology.browser.ui.ParagraphRenderable;
import org.terasology.browser.ui.style.FallbackTextRenderStyle;
import org.terasology.browser.ui.style.TextRenderStyle;
import org.terasology.flow.FlowLineBuilder;
import org.terasology.flow.LaidFlowLine;
import org.terasology.math.Rect2i;
import org.terasology.rendering.assets.font.Font;
import org.terasology.rendering.nui.Canvas;
import org.terasology.rendering.nui.Color;
import org.terasology.rendering.nui.TextLineBuilder;

import java.util.List;

public class HyperlinkableTextParagraphRenderable implements ParagraphRenderable {
    private HyperlinkParagraphData hyperlinkParagraphData;

    public HyperlinkableTextParagraphRenderable(HyperlinkParagraphData hyperlinkParagraphData) {
        this.hyperlinkParagraphData = hyperlinkParagraphData;
    }

    @Override
    public int getMinWidth(Canvas canvas, TextRenderStyle defaultStyle) {
        int minWidth = 0;
        for (HyperlinkParagraphData.HyperlinkParagraphElement element : hyperlinkParagraphData.getElements()) {
            minWidth = Math.max(minWidth, element.getMinWidth(defaultStyle));
        }

        return minWidth;
    }

    @Override
    public void render(Canvas canvas, Rect2i region, TextRenderStyle defaultStyle, HyperlinkRegister hyperlinkRegister) {
        int y = 0;

        for (LaidFlowLine<HyperlinkParagraphData.HyperlinkParagraphElement> line : FlowLineBuilder.getLines(hyperlinkParagraphData.getElements(), defaultStyle, region.width())) {
            int x = 0;

            int height = line.getHeight();

            for (HyperlinkParagraphData.HyperlinkParagraphElement hyperlinkParagraphElement : line.getFlowRenderables()) {
                int elementWidth = hyperlinkParagraphElement.getWidth(defaultStyle);
                Rect2i elementRegion = Rect2i.createFromMinAndSize(region.minX() + x, region.minY() + y, elementWidth, height);
                if (hyperlinkParagraphElement.hyperlink != null) {
                    hyperlinkRegister.registerHyperlink(elementRegion, hyperlinkParagraphElement.hyperlink);
                }
                hyperlinkParagraphElement.render(canvas, elementRegion,
                        defaultStyle);
                x+=elementWidth;
            }

            y+=height;
        }
    }

    @Override
    public int getPreferredHeight(Canvas canvas, TextRenderStyle defaultStyle, int width) {
        int height = 0;
        for (LaidFlowLine<HyperlinkParagraphData.HyperlinkParagraphElement> element : FlowLineBuilder.getLines(hyperlinkParagraphData.getElements(), defaultStyle, width)) {
            height+=element.getHeight();
        }
        return height;
    }
}
