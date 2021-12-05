// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package com.gempukku.lang.parser;

public class Term {
    public enum Type {
        PROGRAM, STRING, COMMENT
    }

    private Type type;
    private String value;
    private int line;
    private int column;

    public Term(Type type, String value, int line, int column) {
        this.type = type;
        this.value = value;
        this.line = line;
        this.column = column;
    }

    public Type getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String val, int columnIncr) {
        this.value = val;
        column += columnIncr;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }
}
