// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package com.gempukku.lang.parser;

public interface ScriptParsingCallback {
    public enum Type {LITERAL, CONSTANT, KEYWORD, COMMENT, VARIABLE}
    public void parsed(int line, int column, int length, Type type);
}
