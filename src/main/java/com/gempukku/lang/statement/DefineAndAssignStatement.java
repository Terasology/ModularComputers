package com.gempukku.lang.statement;

import com.gempukku.lang.DefiningExecutableStatement;
import com.gempukku.lang.ExecutableStatement;
import com.gempukku.lang.Execution;
import com.gempukku.lang.IllegalSyntaxException;
import com.gempukku.lang.execution.DefineAndAssignExecution;

public class DefineAndAssignStatement implements DefiningExecutableStatement {
    private String _name;
    private ExecutableStatement _value;

    public DefineAndAssignStatement(String name, ExecutableStatement value) throws IllegalSyntaxException {
        _name = name;
        _value = value;
    }

    @Override
    public String getDefinedVariableName() {
        return _name;
    }

    @Override
    public Execution createExecution() {
        return new DefineAndAssignExecution(_name, _value);
    }

    @Override
    public boolean requiresSemicolon() {
        return true;
    }
}
