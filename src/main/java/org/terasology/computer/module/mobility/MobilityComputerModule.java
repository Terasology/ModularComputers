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
package org.terasology.computer.module.mobility;

import org.terasology.computer.system.server.lang.ComputerModule;
import org.terasology.computer.system.server.lang.ModuleMethodExecutable;
import org.terasology.engine.Time;
import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.world.BlockEntityRegistry;
import org.terasology.world.WorldProvider;
import org.terasology.world.block.BlockManager;

import java.util.Collection;

public class MobilityComputerModule implements ComputerModule {
    private WorldProvider worldProvider;
    private BlockManager blockManager;
    private EntityRef replacingInstigator;
    private EntityManager entityManager;
    private BlockEntityRegistry blockEntityRegistry;
    private Time time;

    private String moduleType;
    private String moduleName;

    public MobilityComputerModule(WorldProvider worldProvider, BlockManager blockManager, EntityRef replacingInstigator, EntityManager entityManager, BlockEntityRegistry blockEntityRegistry,
                                  Time time, String moduleType, String moduleName) {
        this.worldProvider = worldProvider;
        this.blockManager = blockManager;
        this.replacingInstigator = replacingInstigator;
        this.entityManager = entityManager;
        this.blockEntityRegistry = blockEntityRegistry;
        this.time = time;
        this.moduleType = moduleType;
        this.moduleName = moduleName;
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
        return true;
    }

    @Override
    public boolean acceptsNewModule(ComputerModule computerModule) {
        return true;
    }

    @Override
    public ModuleMethodExecutable getFunctionByName(String name) {
        if (name.equals("move")) {
            return new MoveMethod(worldProvider, entityManager, blockManager, blockEntityRegistry, replacingInstigator, time);
        }
        return null;
    }
}
