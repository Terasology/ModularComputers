// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.parser;

import java.util.ArrayList;
import java.util.List;

public class TermBlock {
    private Term _term;
    private List<TermBlock> _termBlocks;
    private int _blockStartLine;
    private int _blockStartColumn;
    private int _blockEndLine;
    private int _blockEndColumn;

    public TermBlock(int blockStartLine, int blockStartColumn) {
        _blockStartLine = blockStartLine;
        _blockStartColumn = blockStartColumn;
        _termBlocks = new ArrayList<TermBlock>();
    }

    private TermBlock(Term term) {
        _term = term;
    }

    public void terminateTermBlock(int blockEndLine, int blockEndColumn) {
        _blockEndLine = blockEndLine;
        _blockEndColumn = blockEndColumn;
    }

    public int getBlockEndColumn() {
        return _blockEndColumn;
    }

    public int getBlockEndLine() {
        return _blockEndLine;
    }

    public int getBlockStartColumn() {
        return _blockStartColumn;
    }

    public int getBlockStartLine() {
        return _blockStartLine;
    }

    public void addTermBlock(TermBlock termBlock) {
        _termBlocks.add(termBlock);
    }

    public void addTermBlock(Term term) {
        _termBlocks.add(new TermBlock(term));
    }

    public boolean isTerm() {
        return _term != null;
    }

    public Term getTerm() {
        return _term;
    }

    public List<TermBlock> getTermBlocks() {
        return _termBlocks;
    }
}
