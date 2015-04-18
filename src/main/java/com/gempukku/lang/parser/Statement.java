package com.gempukku.lang.parser;

import java.util.LinkedList;
import java.util.List;

public class Statement {
    private List<TermBlock> _termBlocks = new LinkedList<TermBlock>();

    public void addTermBlock(TermBlock termBlock) {
        _termBlocks.add(termBlock);
    }

    public List<TermBlock> getTermBlocks() {
        return _termBlocks;
    }
}
