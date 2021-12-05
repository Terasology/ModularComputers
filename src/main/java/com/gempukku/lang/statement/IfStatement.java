// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package com.gempukku.lang.statement;

import com.gempukku.lang.ExecutableStatement;
import com.gempukku.lang.Execution;
import com.gempukku.lang.execution.IfExecution;

import java.util.ArrayList;
import java.util.List;

public class IfStatement implements ExecutableStatement {
    private List<ConditionStatement> conditionStatements = new ArrayList<ConditionStatement>();
    private ExecutableStatement elseStatement;
    private int line;

    public IfStatement(int line, ExecutableStatement condition, ExecutableStatement statement) {
        this.line = line;
        conditionStatements.add(new ConditionStatement(condition, statement));
    }

    @Override
    public Execution createExecution() {
        return new IfExecution(line, conditionStatements, elseStatement);
    }

    @Override
    public boolean requiresSemicolon() {
        return false;
    }

    public void addElseIf(ExecutableStatement condition, ExecutableStatement statement) {
        conditionStatements.add(new ConditionStatement(condition, statement));
    }

    public void addElse(ExecutableStatement statement) {
        elseStatement = statement;
    }

    public static final class ConditionStatement {
        private ExecutableStatement condition;
        private ExecutableStatement statement;

        private ConditionStatement(ExecutableStatement condition, ExecutableStatement statement) {
            this.condition = condition;
            this.statement = statement;
        }

        public ExecutableStatement getCondition() {
            return condition;
        }

        public ExecutableStatement getStatement() {
            return statement;
        }
    }
}
