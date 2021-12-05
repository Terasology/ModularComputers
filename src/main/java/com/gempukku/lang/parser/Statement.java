// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package com.gempukku.lang.parser;

import java.util.LinkedList;
import java.util.List;

public class Statement {
    private List<TermBlock> termBlocks = new LinkedList<TermBlock>();

    public void addTermBlock(TermBlock termBlock) {
        termBlocks.add(termBlock);
    }

    public List<TermBlock> getTermBlocks() {
        return termBlocks;
    }
}
