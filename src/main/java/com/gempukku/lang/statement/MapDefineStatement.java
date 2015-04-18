package com.gempukku.lang.statement;

import com.gempukku.lang.ExecutableStatement;
import com.gempukku.lang.Execution;
import com.gempukku.lang.IllegalSyntaxException;
import com.gempukku.lang.execution.MapDefineExecution;

import java.util.LinkedHashMap;
import java.util.Map;

public class MapDefineStatement implements ExecutableStatement {
    private Map<String, ExecutableStatement> _properties = new LinkedHashMap<String, ExecutableStatement>();

    public MapDefineStatement() {

    }

    public void addProperty(int line, int column, String name, ExecutableStatement statement) throws IllegalSyntaxException {
        if (_properties.containsKey(name))
            throw new IllegalSyntaxException(line, column, "This map already contains an entry for this name");

        _properties.put(name, statement);
    }

    @Override
    public Execution createExecution() {
        return new MapDefineExecution(_properties);
    }

    @Override
    public boolean requiresSemicolon() {
        return false;
    }
}
