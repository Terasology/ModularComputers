// Copyright 2021 The Terasology Foundation
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
    private int line;
    private List<IfStatement.ConditionStatement> conditionStatements;
    private ExecutableStatement elseStatement;

    private boolean foundCondition;
    private int nextConditionStackedIndex = 0;
    private int nextStatementStackedIfNeededIndex = 0;
    private boolean elseStacked;

    public IfExecution(int line, List<IfStatement.ConditionStatement> conditionStatements, ExecutableStatement elseStatement) {
        this.line = line;
        this.conditionStatements = conditionStatements;
        this.elseStatement = elseStatement;
    }

    @Override
    public boolean hasNextExecution(ExecutionContext executionContext) {
        if (foundCondition) {
            return false;
        }

        if (nextStatementStackedIfNeededIndex < nextConditionStackedIndex) {
            return true;
        }
        if (nextConditionStackedIndex < conditionStatements.size()) {
            return true;
        }
        return !elseStacked && elseStatement != null;
    }

    @Override
    public ExecutionProgress executeNextStatement(ExecutionContext executionContext, ExecutionCostConfiguration configuration)
            throws ExecutionException {
        if (nextStatementStackedIfNeededIndex < nextConditionStackedIndex) {
            final Variable value = executionContext.getContextValue();
            if (value.getType() != Variable.Type.BOOLEAN) {
                throw new ExecutionException(line, "Condition not of type BOOLEAN");
            }

            nextStatementStackedIfNeededIndex++;
            boolean ifResult = (Boolean) value.getValue();
            if (ifResult) {
                foundCondition = true;
                final ExecutableStatement statement = conditionStatements.get(nextStatementStackedIfNeededIndex - 1).getStatement();
                if (statement != null) {
                    executionContext.stackExecution(statement.createExecution());
                }
                return new ExecutionProgress(configuration.getGetContextValue() + configuration.getStackExecution());
            } else {
                return new ExecutionProgress(configuration.getGetContextValue());
            }
        }
        if (nextConditionStackedIndex < conditionStatements.size()) {
            executionContext.stackExecution(conditionStatements.get(nextConditionStackedIndex).getCondition().createExecution());
            nextConditionStackedIndex++;
            return new ExecutionProgress(configuration.getStackExecution());
        }
        if (!elseStacked) {
            executionContext.stackExecution(elseStatement.createExecution());
            elseStacked = true;
            return new ExecutionProgress(configuration.getStackExecution());
        }
        return null;
    }
}
