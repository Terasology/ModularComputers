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
package org.terasology.computer.context;

import com.gempukku.lang.ExecutionContext;
import com.gempukku.lang.ExecutionCostConfiguration;
import org.terasology.computer.component.ComputerComponent;
import org.terasology.computer.system.server.lang.ModuleComputerCallback;
import org.terasology.entitySystem.entity.EntityRef;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class TerasologyComputerExecutionContext extends ExecutionContext {
    private ComputerCallback computerCallback;
    private EntityRef computerEntity;

    public TerasologyComputerExecutionContext(ExecutionCostConfiguration configuration, ComputerCallback computerCallback,
                                              EntityRef computerEntity) {
        super(configuration);
        this.computerCallback = computerCallback;
        this.computerEntity = computerEntity;
    }

    public ComputerCallback getComputerCallback() {
        return computerCallback;
    }

    public ModuleComputerCallback getModuleComputerCallback(int slot) {
        return new ModuleComputerCallback() {
            @Override
            public Map<String, String> getModuleData() {
                ComputerComponent computerComponent = computerEntity.getComponent(ComputerComponent.class);
                List<Map<String, String>> moduleData = computerComponent.moduleData;
                if (moduleData.size()<=slot) {
                    return Collections.emptyMap();
                }

                return Collections.unmodifiableMap(moduleData.get(slot));
            }

            @Override
            public void setModuleData(Map<String, String> moduleData) {
                ComputerComponent computerComponent = computerEntity.getComponent(ComputerComponent.class);
                List<Map<String, String>> modulesData = computerComponent.moduleData;
                modulesData.set(slot, moduleData);

                computerEntity.saveComponent(computerComponent);
            }
        };
    }
}
