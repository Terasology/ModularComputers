// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.parser;

import java.util.LinkedList;
import java.util.List;

public class Statement {
    private final List<TermBlock> _termBlocks = new LinkedList<TermBlock>();

    public void addTermBlock(TermBlock termBlock) {
        _termBlocks.add(termBlock);
    }

    public List<TermBlock> getTermBlocks() {
        return _termBlocks;
    }
}
