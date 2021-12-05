// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.computer.system.server.lang;

import org.terasology.computer.ui.documentation.DefaultMethodDocumentation;
import org.terasology.computer.ui.documentation.DocumentationBuilder;
import org.terasology.computer.ui.documentation.MethodDocumentation;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public abstract class AbstractModuleMethodExecutable<T> implements ModuleMethodExecutable<T> {
    private DefaultMethodDocumentation methodDocumentation;
    private Set<String> parameters = new LinkedHashSet<>();

    public AbstractModuleMethodExecutable(String simpleDocumentation) {
        this(simpleDocumentation, null, null);
    }

    public AbstractModuleMethodExecutable(String simpleDocumentation, String returnType, String returnDocumentation) {
        methodDocumentation = new DefaultMethodDocumentation(simpleDocumentation, returnType, returnDocumentation);
    }

    public void addParameter(String name, String type, String documentation) {
        parameters.add(name);
        methodDocumentation.addParameterDocumentation(name, type, documentation);
    }

    public void addExample(String description, String code) {
        methodDocumentation.addExample(
                DocumentationBuilder.createExampleParagraphs(
                        description, code));
    }

    @Override
    public final Collection<String> getParameterNames() {
        return Collections.unmodifiableCollection(parameters);
    }

    @Override
    public final MethodDocumentation getMethodDocumentation() {
        return methodDocumentation;
    }
}
