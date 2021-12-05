// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package com.gempukku.lang.statement;

import com.gempukku.lang.ExecutableStatement;
import com.gempukku.lang.Execution;

public class NamedStatement implements ExecutableStatement {
    private String name;

    public NamedStatement(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public Execution createExecution() {
        return null;
    }

    @Override
    public boolean requiresSemicolon() {
        return false;
    }
}
