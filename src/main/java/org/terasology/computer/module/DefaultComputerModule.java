// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
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
