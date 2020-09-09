// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package com.gempukku.lang.execution;

import com.gempukku.lang.ExecutableStatement;
import com.gempukku.lang.Execution;
import com.gempukku.lang.ExecutionContext;
import com.gempukku.lang.ExecutionCostConfiguration;
import com.gempukku.lang.ExecutionException;
import com.gempukku.lang.ExecutionProgress;
import com.gempukku.lang.Variable;
import com.gempukku.lang.statement.IfStatement;

import java.util.List;

public class IfExecution implements Execution {
    private final int _line;
    private final List<IfStatement.ConditionStatement> _conditionStatements;
    private final ExecutableStatement _elseStatement;

    private boolean _foundCondition;
    private int _nextConditionStackedIndex = 0;
    private int _nextStatementStackedIfNeededIndex = 0;
    private boolean _elseStacked;

    public IfExecution(int line, List<IfStatement.ConditionStatement> conditionStatements,
                       ExecutableStatement elseStatement) {
        _line = line;
        _conditionStatements = conditionStatements;
        _elseStatement = elseStatement;
    }

    @Override
    public boolean hasNextExecution(ExecutionContext executionContext) {
        if (_foundCondition)
            return false;

        if (_nextStatementStackedIfNeededIndex < _nextConditionStackedIndex)
            return true;
        if (_nextConditionStackedIndex < _conditionStatements.size())
            return true;
        return !_elseStacked && _elseStatement != null;
    }

    @Override
    public ExecutionProgress executeNextStatement(ExecutionContext executionContext,
                                                  ExecutionCostConfiguration configuration) throws ExecutionException {
        if (_nextStatementStackedIfNeededIndex < _nextConditionStackedIndex) {
            final Variable value = executionContext.getContextValue();
            if (value.getType() != Variable.Type.BOOLEAN)
                throw new ExecutionException(_line, "Condition not of type BOOLEAN");

            _nextStatementStackedIfNeededIndex++;
            boolean ifResult = (Boolean) value.getValue();
            if (ifResult) {
                _foundCondition = true;
                final ExecutableStatement statement =
                        _conditionStatements.get(_nextStatementStackedIfNeededIndex - 1).getStatement();
                if (statement != null)
                    executionContext.stackExecution(statement.createExecution());
                return new ExecutionProgress(configuration.getGetContextValue() + configuration.getStackExecution());
            } else
                return new ExecutionProgress(configuration.getGetContextValue());
        }
        if (_nextConditionStackedIndex < _conditionStatements.size()) {
            executionContext.stackExecution(_conditionStatements.get(_nextConditionStackedIndex).getCondition().createExecution());
            _nextConditionStackedIndex++;
            return new ExecutionProgress(configuration.getStackExecution());
        }
        if (!_elseStacked) {
            executionContext.stackExecution(_elseStatement.createExecution());
            _elseStacked = true;
            return new ExecutionProgress(configuration.getStackExecution());
        }
        return null;
    }
}
