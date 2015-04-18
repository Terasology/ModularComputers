package com.gempukku.lang.statement;

import com.gempukku.lang.CallContext;
import com.gempukku.lang.DefaultFunctionExecutable;
import com.gempukku.lang.DefiningExecutableStatement;
import com.gempukku.lang.ExecutableStatement;
import com.gempukku.lang.Execution;
import com.gempukku.lang.ExecutionContext;
import com.gempukku.lang.ExecutionCostConfiguration;
import com.gempukku.lang.ExecutionException;
import com.gempukku.lang.ExecutionProgress;
import com.gempukku.lang.IllegalSyntaxException;
import com.gempukku.lang.Variable;
import com.gempukku.lang.execution.SimpleExecution;

import java.util.List;

public class DefineFunctionStatement implements DefiningExecutableStatement {
    private String _name;
    private List<String> _parameterNames;
    private List<ExecutableStatement> _statements;

    public DefineFunctionStatement(String name, List<String> parameterNames, List<ExecutableStatement> statements) throws IllegalSyntaxException {
        _name = name;
        _parameterNames = parameterNames;
        _statements = statements;
    }

    @Override
    public String getDefinedVariableName() {
        return _name;
    }

    @Override
    public Execution createExecution() {
        return new SimpleExecution() {
            @Override
            protected ExecutionProgress execute(ExecutionContext context, ExecutionCostConfiguration configuration) throws ExecutionException {
                final DefaultFunctionExecutable functionExecutable = new DefaultFunctionExecutable(context.peekCallContext(), _parameterNames.toArray(new String[_parameterNames.size()]));
                functionExecutable.setStatement(
                        new BlockStatement(_statements, false, true));
                final CallContext callContext = context.peekCallContext();
                final Variable variable = callContext.defineVariable(_name);
                context.setVariableValue(variable, functionExecutable);
                return new ExecutionProgress(configuration.getSetVariable());
            }
        };
    }

    @Override
    public boolean requiresSemicolon() {
        return false;
    }
}
