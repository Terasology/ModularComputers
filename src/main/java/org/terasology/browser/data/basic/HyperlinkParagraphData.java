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

import org.terasology.browser.data.ParagraphData;
import org.terasology.browser.ui.ParagraphRenderable;
import org.terasology.browser.ui.style.FallbackTextRenderStyle;
import org.terasology.browser.ui.style.ParagraphRenderStyle;
import org.terasology.browser.ui.style.TextRenderStyle;
import org.terasology.flow.FlowRenderable;
import org.terasology.math.Rect2i;
import org.terasology.rendering.assets.font.Font;
import org.terasology.rendering.nui.Canvas;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class HyperlinkParagraphData implements ParagraphData {
    private ParagraphRenderStyle paragraphRenderStyle;
    private List<HyperlinkParagraphElement> data = new LinkedList<>();

    public HyperlinkParagraphData(ParagraphRenderStyle paragraphRenderStyle) {
        this.paragraphRenderStyle = paragraphRenderStyle;
    }

    @Override
    public ParagraphRenderStyle getParagraphRenderStyle() {
        return paragraphRenderStyle;
    }

    @Override
    public ParagraphRenderable getParagraphContents() {
        return new HyperlinkableTextParagraphRenderable(this);
    }

    public void append(String text, TextRenderStyle textRenderStyle, String hyperlink) {
        data.add(new HyperlinkParagraphElement(text, textRenderStyle, hyperlink));
    }

    public List<HyperlinkParagraphElement> getElements() {
        return Collections.unmodifiableList(data);
    }

    public class HyperlinkParagraphElement implements FlowRenderable<HyperlinkParagraphElement> {
        public final String text;
        public final TextRenderStyle paragraphRenderStyle;
        public final String hyperlink;

        public HyperlinkParagraphElement(String text, TextRenderStyle paragraphRenderStyle, String hyperlink) {
            this.text = text;
            this.paragraphRenderStyle = paragraphRenderStyle;
            this.hyperlink = hyperlink;
        }

        @Override
        public void render(Canvas canvas, Rect2i bounds, TextRenderStyle defaultRenderStyle) {
            TextRenderStyle textRenderStyle = getTextRenderStyle(defaultRenderStyle);
            canvas.drawTextRaw(text, textRenderStyle.getFont(), textRenderStyle.getColor(), bounds);
        }

        @Override
        public int getMinWidth(TextRenderStyle defaultRenderStyle) {
            TextRenderStyle textRenderStyle = getTextRenderStyle(defaultRenderStyle);
            Font font = textRenderStyle.getFont();

            int minWidth = 0;
            String[] words = text.split(" ");
            for (String word : words) {
                int width = font.getWidth(word);
                minWidth = Math.max(minWidth, width);
            }

            return minWidth;
        }

        @Override
        public int getWidth(TextRenderStyle defaultRenderStyle) {
            TextRenderStyle textRenderStyle = getTextRenderStyle(defaultRenderStyle);
            Font font = textRenderStyle.getFont();

            return font.getWidth(text);
        }

        @Override
        public int getHeight(TextRenderStyle defaultRenderStyle) {
            TextRenderStyle textRenderStyle = getTextRenderStyle(defaultRenderStyle);
            Font font = textRenderStyle.getFont();
            return font.getLineHeight();
        }

        @Override
        public FlowRenderable.SplitResult<HyperlinkParagraphElement> splitAt(TextRenderStyle defaultRenderStyle, int width) {
            TextRenderStyle textRenderStyle = getTextRenderStyle(defaultRenderStyle);
            Font font = textRenderStyle.getFont();
            int wholeTextWidth = font.getWidth(text);
            if (wholeTextWidth <=width)
                return new SplitResult<>(this, null);

            int spaceWidth = font.getWidth(' ');

            boolean first = true;
            int usedSpace = 0;

            StringBuilder before = new StringBuilder();
            StringBuilder after = new StringBuilder();

            boolean appendingToBefore = true;

            String[] words = text.split(" ");
            if (text.startsWith(" ")) {
                before.append(" ");
                usedSpace+=spaceWidth;
            }
            for (String word : words) {
                if (appendingToBefore) {
                    if (!first) {
                        usedSpace+=spaceWidth;
                        before.append(" ");
                    }

                    usedSpace += font.getWidth(word);
                    if (usedSpace>width) {
                        if (before.length() == 0) {
                            return new SplitResult<>(null, this);
                        } else {
                            appendingToBefore = false;
                            after.append(word);
                        }
                    } else {
                        before.append(word);
                    }
                    first = false;
                } else {
                    after.append(" ");
                    after.append(word);
                }
            }
            if (text.endsWith(" ")) {
                after.append(" ");
            }

            String beforeText = trimRight(before).toString();
            String afterText = trimLeft(after).toString();

            if (afterText.isEmpty()) {
                return new SplitResult<>(
                        new HyperlinkParagraphElement(beforeText, paragraphRenderStyle, hyperlink), null);
            } else {
                return new SplitResult<>(
                        new HyperlinkParagraphElement(beforeText, paragraphRenderStyle, hyperlink),
                        new HyperlinkParagraphElement(afterText, paragraphRenderStyle, hyperlink));
            }
        }

        private TextRenderStyle getTextRenderStyle(TextRenderStyle defaultRenderStyle) {
            if (paragraphRenderStyle == null) {
                return defaultRenderStyle;
            }
            return new FallbackTextRenderStyle(paragraphRenderStyle, defaultRenderStyle);
        }

        private StringBuilder trimRight(StringBuilder stringBuilder) {
            int size = stringBuilder.length();
            for (int i=size-1; i>=0; i--) {
                if (stringBuilder.charAt(i) != ' ') {
                    stringBuilder.replace(i+1, size, "");
                    break;
                }
            }
            return stringBuilder;
        }

        private StringBuilder trimLeft(StringBuilder stringBuilder) {
            int size = stringBuilder.length();
            for (int i=0; i<size; i++) {
                if (stringBuilder.charAt(i) != ' ') {
                    stringBuilder.replace(0, i, "");
                    break;
                }
            }
            return stringBuilder;
        }
    }
}
