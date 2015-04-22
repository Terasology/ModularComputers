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
package org.terasology.computer.system.common;

import org.terasology.computer.system.server.lang.ComputerModule;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.registry.Share;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RegisterSystem(RegisterMode.ALWAYS)
@Share(value = ComputerModuleRegistry.class)
public class ComputerCommonSystem extends BaseComponentSystem implements ComputerModuleRegistry {
    private Map<String, ComputerModule> computerModuleRegistry = new HashMap<>();

    @Override
    public void registerComputerModule(String type, ComputerModule computerModule) {
        computerModuleRegistry.put(type, computerModule);
    }

    @Override
    public ComputerModule getComputerModuleByType(String type) {
        return computerModuleRegistry.get(type);
    }

    @Override
    public Collection<ComputerModule> getAllRegisteredModules() {
        return Collections.unmodifiableCollection(computerModuleRegistry.values());
    }
}
