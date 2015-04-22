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

import com.gempukku.lang.CallContext;
import com.gempukku.lang.ExecutionContext;
import com.gempukku.lang.ExecutionCostConfiguration;
import com.gempukku.lang.ExecutionException;
import com.gempukku.lang.ExecutionProgress;
import com.gempukku.lang.IllegalSyntaxException;
import com.gempukku.lang.ListPropertyProducer;
import com.gempukku.lang.MapPropertyProducer;
import com.gempukku.lang.ObjectDefinition;
import com.gempukku.lang.ObjectPropertyProducer;
import com.gempukku.lang.ScriptExecutable;
import com.gempukku.lang.StringPropertyProducer;
import com.gempukku.lang.Variable;
import com.gempukku.lang.parser.ScriptParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.browser.data.ParagraphData;
import org.terasology.computer.component.ComputerComponent;
import org.terasology.computer.component.ComputerModuleComponent;
import org.terasology.computer.system.common.ComputerLanguageContext;
import org.terasology.computer.system.common.ComputerLanguageContextInitializer;
import org.terasology.computer.system.common.ComputerModuleRegistry;
import org.terasology.computer.system.server.lang.ComputerModule;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.logic.inventory.InventoryComponent;
import org.terasology.math.geom.Vector3i;
import org.terasology.world.block.BlockComponent;

import java.io.IOException;
import java.io.StringReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ComputerContext {
    private static final Logger logger = LoggerFactory.getLogger(ComputerContext.class);

    private static final int MEMORY_CHECK_INTERVAL = 50;

    private ComputerModuleRegistry computerModuleRegistry;
    private EntityRef entity;
    private int speed;
    private int stackSize;
    private int memory;

    private int memoryConsumptionCheckCounter;

    private int remainingWaitingCpuCycles;
    private int minimumTicksRemaining;

    private ComputerConsole console = new ComputerConsole();

    private ExecutionContext context;
    private AwaitingCondition awaitingCondition;

    private Map<EntityRef, ComputerConsoleListener> consoleListenerMap = new HashMap<>();

    public ComputerContext(ComputerModuleRegistry computerModuleRegistry, EntityRef entity, int speed, int stackSize, int memory) {
        this.computerModuleRegistry = computerModuleRegistry;
        this.entity = entity;
        this.speed = speed;
        this.stackSize = stackSize;
        this.memory = memory;
    }

    public void updateComputerEntity(EntityRef entity) {
        this.entity = entity;
    }

    public void executeContext(float delta) {
        if (context != null) {
            logger.debug("Executing program - minTicksRemaining: "+minimumTicksRemaining+", remainingWaitingCpuCycles: "+remainingWaitingCpuCycles);
            if (minimumTicksRemaining > 0) {
                minimumTicksRemaining--;
            }
            if (remainingWaitingCpuCycles >= speed) {
                remainingWaitingCpuCycles -= speed;
            } else {
                int freeCycles = speed - remainingWaitingCpuCycles;
                remainingWaitingCpuCycles = 0;
                if (minimumTicksRemaining == 0) {
                    executeNextProgramStepUntilRunsOutOfCycles(freeCycles);
                }
            }
        }
    }

    public boolean isRunningProgram() {
        return context != null;
    }

    public void startProgram(String name, String programText, ComputerLanguageContextInitializer computerLanguageContextInitializer, ExecutionCostConfiguration configuration) throws IllegalSyntaxException {
        try {
            logger.debug("starting program: "+name);

            Set<String> variables = new HashSet<>();

            CallContext callContext = new CallContext(null, false, true);
            computerLanguageContextInitializer.initializeContext(
                    new ComputerLanguageContext() {
                        @Override
                        public void addObject(String object, ObjectDefinition objectDefinition, Collection<ParagraphData> objectDescription, Map<String, Collection<ParagraphData>> functionDescriptions, Map<String, Map<String, Collection<ParagraphData>>> functionParametersDescriptions, Map<String, Collection<ParagraphData>> functionReturnDescriptions) {
                            variables.add(object);
                            try {
                                callContext.defineVariable(object).setValue(objectDefinition);
                            } catch (ExecutionException exp) {
                                // Ignore - can't happen
                            }
                        }
                    });

            ScriptExecutable scriptExecutable = new ScriptParser().parseScript(new StringReader(programText), variables);

            ExecutionContext executionContext = new TerasologyComputerExecutionContext(configuration,
                    getComputerCallback());
            executionContext.addPropertyProducer(Variable.Type.MAP, new MapPropertyProducer());
            executionContext.addPropertyProducer(Variable.Type.OBJECT, new ObjectPropertyProducer());
            executionContext.addPropertyProducer(Variable.Type.LIST, new ListPropertyProducer());
            executionContext.addPropertyProducer(Variable.Type.STRING, new StringPropertyProducer());

            executionContext.stackExecutionGroup(callContext, scriptExecutable.createExecution(callContext));

            context = executionContext;

            logger.debug("started program: "+name);
        } catch (IOException exp) {
            // Can't happen - ignore
        }
    }

    public ComputerCallback getComputerCallback() {
        return new ComputerCallback() {
            @Override
            public ComputerConsole getConsole() {
                return console;
            }

            @Override
            public int getModuleSlotsCount() {
                return entity.getComponent(ComputerComponent.class).moduleSlotCount;
            }

            @Override
            public ComputerModule getModule(int slot) {
                ComputerComponent computerComponent = entity.getComponent(ComputerComponent.class);

                int moduleSlotCount = computerComponent.moduleSlotCount;
                if (slot<0 || moduleSlotCount<=slot)
                    return null;

                InventoryComponent inventory = entity.getComponent(InventoryComponent.class);

                EntityRef moduleEntity = inventory.itemSlots.get(computerComponent.moduleSlotStart+slot);
                if (moduleEntity.hasComponent(ComputerModuleComponent.class)) {
                    ComputerModuleComponent moduleComponent = moduleEntity.getComponent(ComputerModuleComponent.class);
                    return computerModuleRegistry.getComputerModuleByType(moduleComponent.moduleType);
                }
                return null;
            }

            @Override
            public void suspendWithCondition(AwaitingCondition condition) {
                awaitingCondition = condition;
            }

            @Override
            public Vector3i getComputerLocation() {
                return entity.getComponent(BlockComponent.class).getPosition();
            }

            @Override
            public EntityRef getComputerEntity() {
                return entity;
            }
        };
    }

    private void executeNextProgramStepUntilRunsOutOfCycles(int freeCycles) {
        while (!context.isFinished()) {
            logger.debug("Executing next step");
            try {
                if (awaitingCondition != null) {
                    if (awaitingCondition.isMet()) {
                        awaitingCondition = null;
                    } else {
                        break;
                    }
                }

                ExecutionProgress executionProgress = context.executeNext();

                if (context.getStackTraceSize() > stackSize)
                    throw new ExecutionException(-1, "StackOverflow");

                if (((++memoryConsumptionCheckCounter)%MEMORY_CHECK_INTERVAL == 0) && context.getMemoryUsage()>memory)
                    throw new ExecutionException(-1, "OutOfMemory");

                freeCycles-=executionProgress.getCost();
                minimumTicksRemaining = executionProgress.getMinExecutionTicks();

                if (freeCycles<=0) {
                    remainingWaitingCpuCycles = -freeCycles;
                }
                if (minimumTicksRemaining>0 || remainingWaitingCpuCycles>0) {
                    // Time to break execution, we've done enough in this computer for this tick
                    break;
                }
            } catch (ExecutionException exp) {
                if (exp.getLine() == -1)
                    console.appendString("ExecutionException[unknown line] - " + exp.getMessage());
                else
                    console.appendString("ExecutionException[line " + exp.getLine() + "] - " + exp.getMessage());
                context = null;
                break;
            }
        }
        if (context != null && context.isFinished()) {
            context = null;
        }
    }

    public void registerConsoleListener(EntityRef client, ComputerConsoleListener listener) {
        if (!consoleListenerMap.containsKey(client)) {
            console.addConsoleListener(listener);
            listener.setScreenState(console.getLines());
            consoleListenerMap.put(client, listener);
        }
    }

    public void deregisterConsoleListener(EntityRef client) {
        ComputerConsoleListener listener = consoleListenerMap.remove(client);
        if (listener != null) {
            console.removeConsoleListener(listener);
        }
    }
}
