// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package com.gempukku.lang.statement;

import com.gempukku.lang.ExecutableStatement;
import com.gempukku.lang.Execution;
import com.gempukku.lang.execution.ListDefineExecution;

import java.util.List;

public class ListDefineStatement implements ExecutableStatement {
    private List<ExecutableStatement> values;

    public ListDefineStatement(List<ExecutableStatement> values) {
        this.values = values;
    }

    @Override
    public boolean requiresSemicolon() {
        return false;
    }

    @Override
    public Execution createExecution() {
        return new ListDefineExecution(values);
    }
}
