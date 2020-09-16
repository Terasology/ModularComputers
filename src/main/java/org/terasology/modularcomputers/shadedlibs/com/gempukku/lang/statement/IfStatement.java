// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.statement;

import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.ExecutableStatement;
import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.Execution;
import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.execution.IfExecution;

import java.util.ArrayList;
import java.util.List;

public class IfStatement implements ExecutableStatement {
    private final List<ConditionStatement> _conditionStatements = new ArrayList<ConditionStatement>();
    private ExecutableStatement _elseStatement;
    private final int _line;

    public IfStatement(int line, ExecutableStatement condition, ExecutableStatement statement) {
        _line = line;
        _conditionStatements.add(new ConditionStatement(condition, statement));
    }

    @Override
    public Execution createExecution() {
        return new IfExecution(_line, _conditionStatements, _elseStatement);
    }

    @Override
    public boolean requiresSemicolon() {
        return false;
    }

    public void addElseIf(ExecutableStatement condition, ExecutableStatement statement) {
        _conditionStatements.add(new ConditionStatement(condition, statement));
    }

    public void addElse(ExecutableStatement statement) {
        _elseStatement = statement;
    }

    public static class ConditionStatement {
        private final ExecutableStatement _condition;
        private final ExecutableStatement _statement;

        private ConditionStatement(ExecutableStatement condition, ExecutableStatement statement) {
            _condition = condition;
            _statement = statement;
        }

        public ExecutableStatement getCondition() {
            return _condition;
        }

        public ExecutableStatement getStatement() {
            return _statement;
        }
    }
}
