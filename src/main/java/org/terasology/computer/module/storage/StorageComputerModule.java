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
package org.terasology.computer.module.storage;

import org.terasology.computer.system.server.lang.ComputerModule;
import org.terasology.computer.system.server.lang.ModuleFunctionExecutable;

import java.util.Collection;

public class StorageComputerModule implements ComputerModule {
    private String moduleType;
    private String moduleName;
    private int slotCount;

    public StorageComputerModule(String moduleType, String moduleName, int slotCount) {
        this.moduleType = moduleType;
        this.moduleName = moduleName;
        this.slotCount = slotCount;
    }

    @Override
    public String getModuleType() {
        return moduleType;
    }

    @Override
    public String getModuleName() {
        return moduleName;
    }

    @Override
    public boolean canBePlacedInComputer(Collection<ComputerModule> computerModulesInstalled) {
        // Only one storage module can be stored in a computer
        for (ComputerModule computerModule : computerModulesInstalled) {
            if (computerModule.getModuleType().equals(moduleType)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean acceptsNewModule(ComputerModule computerModule) {
        return true;
    }

    @Override
    public ModuleFunctionExecutable getFunctionByName(String name) {
        if (name.equals("getInputInventoryBinding")) {
            return new StorageInventoryBindingFunction(true);
        } else if (name.equals("getOutputInventoryBinding")) {
            return new StorageInventoryBindingFunction(false);
        }
        return null;
    }
}
