// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.modularcomputers.shadedlibs.com.gempukku.lang;

public class ExecutionException extends Exception {
    private final int _line;

    public ExecutionException(int line, String message) {
        super(message);
        _line = line;
    }

    public int getLine() {
        return _line;
    }
}
