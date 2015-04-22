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
package org.terasology.browser.ui;

import org.terasology.browser.data.BrowserData;
import org.terasology.browser.data.DocumentData;
import org.terasology.browser.data.ParagraphData;
import org.terasology.browser.ui.style.DefaultDocumentRenderStyle;
import org.terasology.browser.ui.style.DocumentRenderStyle;
import org.terasology.browser.ui.style.FallbackDocumentRenderStyle;
import org.terasology.browser.ui.style.FallbackParagraphRenderStyle;
import org.terasology.browser.ui.style.ParagraphRenderStyle;
import org.terasology.input.MouseInput;
import org.terasology.math.Rect2i;
import org.terasology.math.Vector2i;
import org.terasology.rendering.nui.BaseInteractionListener;
import org.terasology.rendering.nui.Canvas;
import org.terasology.rendering.nui.Color;
import org.terasology.rendering.nui.CoreWidget;
import org.terasology.rendering.nui.FocusManager;
import org.terasology.rendering.nui.InteractionListener;

import java.util.LinkedList;
import java.util.List;

public class BrowserWidget extends CoreWidget {
    private BrowserData browserData;
    private String displayedPage;

    private List<BrowserHyperlinkListener> listenerList = new LinkedList<>();

    private List<HyperlinkBox> hyperlinkBoxes = new LinkedList<>();
    private ParagraphRenderable.HyperlinkRegister register = new HyperlinkRegisterImpl();

    public void setBrowserData(BrowserData browserData) {
        this.browserData = browserData;
    }

    public void addBrowserHyperlinkListener(BrowserHyperlinkListener listener) {
        listenerList.add(listener);
    }

    @Override
    public void onDraw(Canvas canvas) {
        hyperlinkBoxes.clear();
        if (displayedPage != null) {
            Rect2i region = canvas.getRegion();
            int y = region.minY();

            DefaultDocumentRenderStyle defaultDocumentRenderStyle = new DefaultDocumentRenderStyle(canvas);

            DocumentData document = browserData.getDocument(displayedPage);
            DocumentRenderStyle documentRenderStyle = getDocumentRenderStyle(defaultDocumentRenderStyle, document);

            Color backgroundColor = documentRenderStyle.getBackgroundColor();
            canvas.drawFilledRectangle(canvas.getRegion(), backgroundColor);

            ParagraphRenderStyle lastRenderStyle = null;
            boolean first = true;
            for (ParagraphData paragraphData : document.getParagraphs()) {
                if (lastRenderStyle != null) {
                    y += lastRenderStyle.getIndentBelow(false);
                }
                ParagraphRenderStyle paragraphRenderStyle = getParagraphRenderStyle(documentRenderStyle, paragraphData);
                y += paragraphRenderStyle.getIndentAbove(first);

                int paragraphWidth = region.width() - paragraphRenderStyle.getIndentLeft() - paragraphRenderStyle.getIndentRight();

                ParagraphRenderable paragraphContents = paragraphData.getParagraphContents();
                int paragraphHeight = paragraphContents.getPreferredHeight(canvas, paragraphRenderStyle, paragraphWidth);
                paragraphContents.render(canvas,
                        Rect2i.createFromMinAndSize(region.minX() + paragraphRenderStyle.getIndentLeft(), y,
                                paragraphWidth, paragraphHeight), paragraphRenderStyle, register);
                y += paragraphHeight;

                lastRenderStyle = paragraphRenderStyle;
                first = false;
            }
        }
        canvas.addInteractionRegion(
                new BaseInteractionListener() {
                    @Override
                    public boolean onMouseClick(MouseInput button, Vector2i pos) {
                        for (HyperlinkBox hyperlinkBox : hyperlinkBoxes) {
                            if (hyperlinkBox.box.contains(pos)) {
                                for (BrowserHyperlinkListener browserHyperlinkListener : listenerList) {
                                    browserHyperlinkListener.hyperlinkClicked(hyperlinkBox.hyperlink);
                                }

                                break;
                            }
                        }

                        return true;
                    }
                });
    }

    private DocumentRenderStyle getDocumentRenderStyle(DefaultDocumentRenderStyle defaultDocumentRenderStyle, DocumentData document) {
        DocumentRenderStyle documentStyle = document.getDocumentRenderStyle();
        if (documentStyle == null) {
            return defaultDocumentRenderStyle;
        }
        return new FallbackDocumentRenderStyle(documentStyle, defaultDocumentRenderStyle);
    }

    @Override
    public Vector2i getPreferredContentSize(Canvas canvas, Vector2i sizeHint) {
        int x = canvas.getRegion().sizeX();
        int y = 0;

        if (displayedPage != null) {
            DefaultDocumentRenderStyle defaultDocumentRenderStyle = new DefaultDocumentRenderStyle(canvas);

            DocumentData document = browserData.getDocument(displayedPage);
            DocumentRenderStyle documentRenderStyle = getDocumentRenderStyle(defaultDocumentRenderStyle, document);

            ParagraphRenderStyle lastRenderStyle = null;
            boolean first = true;
            for (ParagraphData paragraphData : document.getParagraphs()) {
                if (lastRenderStyle != null) {
                    y += lastRenderStyle.getIndentBelow(false);
                }
                ParagraphRenderStyle paragraphRenderStyle = getParagraphRenderStyle(documentRenderStyle, paragraphData);
                y += paragraphRenderStyle.getIndentAbove(first);

                int sideIndent = paragraphRenderStyle.getIndentLeft() + paragraphRenderStyle.getIndentRight();
                y += paragraphData.getParagraphContents().getPreferredHeight(canvas, paragraphRenderStyle, x - sideIndent);

                lastRenderStyle = paragraphRenderStyle;
                first = false;
            }
            if (lastRenderStyle != null) {
                y += lastRenderStyle.getIndentBelow(true);
            }
        }
        return new Vector2i(x, y);
    }

    private ParagraphRenderStyle getParagraphRenderStyle(DocumentRenderStyle documentRenderStyle, ParagraphData paragraphData) {
        ParagraphRenderStyle paragraphStyle = paragraphData.getParagraphRenderStyle();
        if (paragraphStyle == null) {
            return documentRenderStyle;
        }
        return new FallbackParagraphRenderStyle(paragraphStyle, documentRenderStyle);
    }

    public void navigateTo(String pageId) {
        this.displayedPage = pageId;
    }

    private class HyperlinkBox {
        private Rect2i box;
        private String hyperlink;

        private HyperlinkBox(Rect2i box, String hyperlink) {
            this.box = box;
            this.hyperlink = hyperlink;
        }
    }

    private class HyperlinkRegisterImpl implements ParagraphRenderable.HyperlinkRegister {
        @Override
        public void registerHyperlink(Rect2i region, String hyperlink) {
            hyperlinkBoxes.add(new HyperlinkBox(region, hyperlink));
        }
    }
}
