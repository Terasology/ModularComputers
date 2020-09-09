// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package com.gempukku.lang.parser;

public class Term {
    private final Type _type;
    private String _value;
    private final int _line;
    private int _column;
    public Term(Type type, String value, int line, int column) {
        _type = type;
        _value = value;
        _line = line;
        _column = column;
    }

    public Type getType() {
        return _type;
    }

    public String getValue() {
        return _value;
    }

    public void setValue(String value, int columnIncr) {
        _value = value;
        _column += columnIncr;
    }

    public int getLine() {
        return _line;
    }

    public int getColumn() {
        return _column;
    }

    public enum Type {PROGRAM, STRING, COMMENT}
}
