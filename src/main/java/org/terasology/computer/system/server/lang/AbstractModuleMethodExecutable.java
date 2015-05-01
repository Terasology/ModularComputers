/*
 * Copyright 2015 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
