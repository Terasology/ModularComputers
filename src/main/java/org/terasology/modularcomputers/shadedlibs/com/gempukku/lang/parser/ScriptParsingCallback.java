// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.parser;

public interface ScriptParsingCallback {
    void parsed(int line, int column, int length, Type type);

    enum Type {LITERAL, CONSTANT, KEYWORD, COMMENT, VARIABLE}
}
