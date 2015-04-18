package com.gempukku.lang;

public class ExecutionException extends Exception {
    private int _line;

    public ExecutionException(int line, String message) {
        super(message);
        _line = line;
    }

    public int getLine() {
        return _line;
    }
}
