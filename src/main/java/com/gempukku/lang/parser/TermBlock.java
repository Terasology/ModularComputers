// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package com.gempukku.lang.parser;

import java.util.ArrayList;
import java.util.List;

public class TermBlock {
    private Term term;
    private List<TermBlock> termBlocks;
    private int blockStartLine;
    private int blockStartColumn;
    private int blockEndLine;
    private int blockEndColumn;

    public TermBlock(int blockStartLine, int blockStartColumn) {
        this.blockStartLine = blockStartLine;
        this.blockStartColumn = blockStartColumn;
        termBlocks = new ArrayList<TermBlock>();
    }

    private TermBlock(Term term) {
        this.term = term;
    }

    public void terminateTermBlock(int blockEndLine, int blockEndColumn) {
        this.blockEndLine = blockEndLine;
        this.blockEndColumn = blockEndColumn;
    }

    public int getBlockEndColumn() {
        return blockEndColumn;
    }

    public int getBlockEndLine() {
        return blockEndLine;
    }

    public int getBlockStartColumn() {
        return blockStartColumn;
    }

    public int getBlockStartLine() {
        return blockStartLine;
    }

    public void addTermBlock(TermBlock termBlock) {
        termBlocks.add(termBlock);
    }

    public void addTermBlock(Term term) {
        termBlocks.add(new TermBlock(term));
    }

    public boolean isTerm() {
        return term != null;
    }

    public Term getTerm() {
        return term;
    }

    public List<TermBlock> getTermBlocks() {
        return termBlocks;
    }
}
