// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.computer.system.server.lang;

import com.gempukku.lang.CallContext;
import com.gempukku.lang.DelayedExecution;
import com.gempukku.lang.Execution;
import com.gempukku.lang.ExecutionContext;
import com.gempukku.lang.ExecutionCostConfiguration;
import com.gempukku.lang.ExecutionException;
import com.gempukku.lang.ExecutionProgress;
import com.gempukku.lang.Variable;
import com.gempukku.lang.execution.SimpleExecution;
import org.terasology.computer.context.ComputerCallback;
import org.terasology.computer.context.TerasologyComputerExecutionContext;
import org.terasology.computer.system.common.DocumentedFunctionExecutable;
import org.terasology.computer.ui.documentation.DefaultMethodDocumentation;
import org.terasology.computer.ui.documentation.DocumentationBuilder;
import org.terasology.computer.ui.documentation.MethodDocumentation;
import org.terasology.engine.rendering.nui.widgets.browser.data.ParagraphData;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public abstract class TerasologyFunctionExecutable implements DocumentedFunctionExecutable {
    private DefaultMethodDocumentation methodDocumentation;
    private Set<String> parameterNames = new LinkedHashSet<>();

    protected TerasologyFunctionExecutable(String simpleDocumentation) {
        methodDocumentation = new DefaultMethodDocumentation(simpleDocumentation);
    }

    protected TerasologyFunctionExecutable(String simpleDocumentation, String returnType, String returnDocumentation) {
        methodDocumentation = new DefaultMethodDocumentation(simpleDocumentation, returnType, returnDocumentation);
    }

    protected void addParameter(String name, String type, String documentation) {
        parameterNames.add(name);
        methodDocumentation.addParameterDocumentation(name, type, documentation);
    }

    protected void setPageDocumentation(Collection<ParagraphData> pageDocumentation) {
        methodDocumentation.setPageDocumentation(pageDocumentation);
    }

    protected void addExample(String description, String code) {
        methodDocumentation.addExample(
                DocumentationBuilder.createExampleParagraphs(description, code));
    }

    @Override
    public MethodDocumentation getMethodDocumentation() {
        return methodDocumentation;
    }

    @Override
    public final Collection<String> getParameterNames() {
        return Collections.unmodifiableCollection(parameterNames);
    }

    @Override
    public final Execution createExecution(final int line, ExecutionContext executionContext, CallContext callContext) {
        return new DelayedExecution(getDuration(), 0,
                new SimpleExecution() {
                    @Override
                    protected ExecutionProgress execute(ExecutionContext context, ExecutionCostConfiguration configuration)
                            throws ExecutionException {
                        final TerasologyComputerExecutionContext terasologyExecutionContext = (TerasologyComputerExecutionContext) context;
                        ComputerCallback computer = terasologyExecutionContext.getComputerCallback();

                        Map<String, Variable> parameters = new HashMap<String, Variable>();
                        final CallContext callContext = context.peekCallContext();
                        for (String parameterName : parameterNames) {
                            parameters.put(parameterName, callContext.getVariableValue(parameterName));
                        }

                        context.setReturnValue(new Variable(executeFunction(line, computer, parameters)));
                        return new ExecutionProgress(configuration.getSetReturnValue());
                    }
                });
    }

    @Override
    public final CallContext getCallContext() {
        return new CallContext(null, false, false);
    }

    /**
     * Returns duration of the operation in computer cycles. Used to effectively throttle computer programs.
     *
     * @return Duration in computer cycles.
     */
    protected abstract int getDuration();

    /**
     * Executes this function, gets passed an instance of the computer this function is executed on, as well as
     * parameters passed to the function, as defined by getParameterNames method in this class. The returned object
     * will be placed into context that called this function. It is advisable to use only basic objects as return values.
     * Numbers (int, float), booleans, Strings and null.
     * If an execution of the program should be stopped due to a fatal exception, ExecutionException should be thrown by
     * the method.
     *
     * @param line       Line where the call to the function was made.
     * @param computer   Computer this function is executed on.
     * @param parameters Parameters that were sent to this function.
     * @return Object that has to be set in context of the caller (return value).
     * @throws ExecutionException Fatal exception that will be communicated to the computer console. When thrown,
     *                            the execution of the program will stop.
     */
    protected abstract Object executeFunction(int line, ComputerCallback computer, Map<String, Variable> parameters)
            throws ExecutionException;
}
