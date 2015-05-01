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
package org.terasology.computer.module;

import org.terasology.computer.system.server.lang.ComputerModule;
import org.terasology.computer.system.server.lang.ModuleMethodExecutable;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class DefaultComputerModule implements ComputerModule {
    private String moduleType;
    private String moduleName;
    private Map<String, ModuleMethodExecutable<?>> methods = new HashMap<>();

    public DefaultComputerModule(String moduleType, String moduleName) {
        this.moduleType = moduleType;
        this.moduleName = moduleName;
    }

    protected void addMethod(String name, ModuleMethodExecutable<?> method) {
        methods.put(name, method);
    }

    @Override
    public final String getModuleType() {
        return moduleType;
    }

    @Override
    public final String getModuleName() {
        return moduleName;
    }

    @Override
    public boolean canBePlacedInComputer(Collection<ComputerModule> computerModulesInstalled) {
        return true;
    }

    @Override
    public boolean acceptsNewModule(ComputerModule computerModule) {
        return true;
    }

    @Override
    public final ModuleMethodExecutable getMethodByName(String name) {
        return methods.get(name);
    }

    @Override
    public final Map<String, ModuleMethodExecutable<?>> getAllMethods() {
        return Collections.unmodifiableMap(methods);
    }
}
