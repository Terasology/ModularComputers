package com.gempukku.lang.statement;

import com.gempukku.lang.ExecutableStatement;
import com.gempukku.lang.Execution;
import com.gempukku.lang.execution.ListDefineExecution;

import java.util.List;

public class ListDefineStatement implements ExecutableStatement {
    private List<ExecutableStatement> _values;

    public ListDefineStatement(List<ExecutableStatement> values) {
        _values = values;
    }

    @Override
    public boolean requiresSemicolon() {
        return false;
    }

    @Override
    public Execution createExecution() {
        return new ListDefineExecution(_values);
    }
}
