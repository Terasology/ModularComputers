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
import com.gempukku.lang.ObjectPropertyProducer;
import com.gempukku.lang.ScriptExecutable;
import com.gempukku.lang.StringPropertyProducer;
import com.gempukku.lang.Variable;
import com.gempukku.lang.parser.ScriptParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.rendering.nui.widgets.browser.data.ParagraphData;
import org.terasology.computer.component.ComputerComponent;
import org.terasology.computer.component.ComputerModuleComponent;
import org.terasology.computer.system.common.ComputerLanguageContext;
import org.terasology.computer.system.common.ComputerLanguageContextInitializer;
import org.terasology.computer.system.common.ComputerModuleRegistry;
import org.terasology.computer.system.common.DocumentedObjectDefinition;
import org.terasology.computer.system.server.lang.ComputerModule;
import org.terasology.computer.system.server.lang.os.condition.ResultAwaitingCondition;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.logic.inventory.InventoryComponent;
import org.terasology.logic.location.LocationComponent;
import org.terasology.math.geom.Vector3f;
import org.terasology.world.block.BlockComponent;

import java.io.IOException;
import java.io.StringReader;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
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
    private long lastExecutionTime;
    private long minimumTimeRemaining;

    private ComputerConsole console = new ComputerConsole();

    private ExecutionContext executionContext;
    private ResultAwaitingCondition awaitingCondition;
    private EntityRef executedBy;

    private Map<EntityRef, ComputerConsoleListener> consoleListenerMap = new HashMap<>();

    public ComputerContext(ComputerModuleRegistry computerModuleRegistry, EntityRef entity, int speed, int stackSize, int memory) {
        this.computerModuleRegistry = computerModuleRegistry;
        this.entity = entity;
        this.speed = speed;
        this.stackSize = stackSize;
        this.memory = memory;
    }

    public void updateComputerEntity(EntityRef computerEntity) {
        entity = computerEntity;
    }

    public EntityRef getEntity() {
        return entity;
    }

    public Map<EntityRef, ComputerConsoleListener> getConsoleListenerMap() {
        return Collections.unmodifiableMap(consoleListenerMap);
    }

    public void executeContext() {
        if (executionContext != null) {
            long executionTime = System.currentTimeMillis();
            logger.debug("Executing program - minTicksRemaining: " + minimumTimeRemaining + ", remainingWaitingCpuCycles: " + remainingWaitingCpuCycles);
            if (minimumTimeRemaining > 0) {
                // Decrement by the time between calls
                minimumTimeRemaining -= (executionTime - lastExecutionTime);
            }
            lastExecutionTime = executionTime;
            remainingWaitingCpuCycles -= speed;
            if (remainingWaitingCpuCycles <= 0 && minimumTimeRemaining <= 0) {
                executeNextProgramStepUntilRunsOutOfCycles();
            }
        }
    }

    public boolean isRunningProgram() {
        return executionContext != null;
    }

    public void stopProgram() {
        executionContext = null;
        if (awaitingCondition != null) {
            awaitingCondition.dispose();
        }
        executedBy = null;
        awaitingCondition = null;
    }

    public void startProgram(String name, EntityRef executeIdentity, String programText, String[] params, ComputerLanguageContextInitializer computerLanguageContextInitializer,
                             ExecutionCostConfiguration configuration) throws IllegalSyntaxException {
        try {
            logger.debug("starting program: " + name);

            Set<String> variables = new HashSet<>();

            CallContext callContext = new CallContext(null, false, true);
            computerLanguageContextInitializer.initializeContext(
                    new ComputerLanguageContext() {
                        @Override
                        public void addObject(String object, DocumentedObjectDefinition objectDefinition, String objectDescription,
                                              Collection<ParagraphData> additionalParagraphs) {
                            variables.add(object);
                            try {
                                callContext.defineVariable(object).setValue(objectDefinition);
                            } catch (ExecutionException exp) {
                                // Ignore - can't happen
                                exp.printStackTrace();
                            }
                        }

                        @Override
                        public void addObjectType(String objectType, Collection<ParagraphData> documentation) {
                            // Ignore
                        }

                        @Override
                        public void addComputerModule(ComputerModule computerModule, String description, Collection<ParagraphData> additionalParagraphs) {
                            // Ignore
                        }
                    });
            addParametersToProgram(params, variables, callContext);

            ScriptExecutable scriptExecutable = new ScriptParser().parseScript(new StringReader(programText), variables);

            executionContext = new TerasologyComputerExecutionContext(configuration,
                    getComputerCallback());
            executionContext.addPropertyProducer(Variable.Type.MAP, new MapPropertyProducer());
            executionContext.addPropertyProducer(Variable.Type.OBJECT, new ObjectPropertyProducer());
            executionContext.addPropertyProducer(Variable.Type.LIST, new ListPropertyProducer());
            executionContext.addPropertyProducer(Variable.Type.STRING, new StringPropertyProducer());

            executionContext.stackExecutionGroup(callContext, scriptExecutable.createExecution(callContext));

            executedBy = executeIdentity;

            this.awaitingCondition = null;
            this.remainingWaitingCpuCycles = 0;
            this.minimumTimeRemaining = 0;

            logger.debug("started program: " + name);
        } catch (IOException exp) {
            // Can't happen - ignore
            exp.printStackTrace();
        }
    }

    private void addParametersToProgram(String[] params, Set<String> variables, CallContext callContext) {
        variables.add("args");
        List<Variable> args = new LinkedList<>();
        for (String param : params) {
            args.add(new Variable(param));
        }

        try {
            callContext.defineVariable("args").setValue(args);
        } catch (ExecutionException exp) {
            // Ignore - can't happen
            exp.printStackTrace();
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
                if (slot < 0 || moduleSlotCount <= slot) {
                    return null;
                }

                InventoryComponent inventory = entity.getComponent(InventoryComponent.class);

                EntityRef moduleEntity = inventory.itemSlots.get(computerComponent.moduleSlotStart + slot);
                if (moduleEntity.hasComponent(ComputerModuleComponent.class)) {
                    ComputerModuleComponent moduleComponent = moduleEntity.getComponent(ComputerModuleComponent.class);
                    return computerModuleRegistry.getComputerModuleByType(moduleComponent.moduleType);
                }
                return null;
            }

            @Override
            public void suspendWithCondition(ResultAwaitingCondition condition) {
                awaitingCondition = condition;
            }

            @Override
            public Vector3f getComputerLocation() {
                BlockComponent block = entity.getComponent(BlockComponent.class);
                if (block != null) {
                    return block.getPosition().toVector3f();
                }
                return entity.getComponent(LocationComponent.class).getWorldPosition();
            }

            @Override
            public EntityRef getComputerEntity() {
                return entity;
            }

            @Override
            public EntityRef getExecutedBy() {
                return executedBy;
            }
        };
    }

    private void executeNextProgramStepUntilRunsOutOfCycles() {
        while (!executionContext.isFinished()) {
            logger.debug("Executing next step");
            try {
                if (awaitingCondition != null) {
                    if (awaitingCondition.isMet()) {
                        executionContext.setContextValue(awaitingCondition.getReturnValue());
                        awaitingCondition.dispose();
                        awaitingCondition = null;
                    } else {
                        break;
                    }
                }

                ExecutionProgress executionProgress = executionContext.executeNext();

                if (executionContext.getStackTraceSize() > stackSize) {
                    throw new ExecutionException(-1, "StackOverflow");
                }

                if (((++memoryConsumptionCheckCounter) % MEMORY_CHECK_INTERVAL == 0) && executionContext.getMemoryUsage() > memory) {
                    throw new ExecutionException(-1, "OutOfMemory");
                }

                remainingWaitingCpuCycles += executionProgress.getCost();
                minimumTimeRemaining = executionProgress.getMinExecutionTime();

                if (minimumTimeRemaining > 0 || remainingWaitingCpuCycles > 0) {
                    // Time to break execution, we've done enough in this computer for this tick
                    break;
                }
            } catch (ExecutionException exp) {
                if (exp.getLine() == -1) {
                    console.appendString("ExecutionException[unknown line] - " + exp.getMessage());
                } else {
                    console.appendString("ExecutionException[line " + exp.getLine() + "] - " + exp.getMessage());
                }
                stopProgram();
                break;
            }
        }
        if (executionContext != null && executionContext.isFinished()) {
            stopProgram();
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
