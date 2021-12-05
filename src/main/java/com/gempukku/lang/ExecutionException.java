// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package com.gempukku.lang;

public class ExecutionException extends Exception {
    private final int line;

    public ExecutionException(int line, String message) {
        super(message);
        this.line = line;
    }

    public int getLine() {
        return line;
    }
}
