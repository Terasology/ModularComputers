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
package org.terasology.computer.system.server;

import com.gempukku.lang.ExecutionCostConfiguration;
import com.gempukku.lang.IllegalSyntaxException;
import com.gempukku.lang.ObjectDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.computer.component.ComputerComponent;
import org.terasology.computer.component.ComputerModuleComponent;
import org.terasology.computer.component.ComputerSystemComponent;
import org.terasology.computer.context.ComputerCallback;
import org.terasology.computer.context.ComputerContext;
import org.terasology.computer.event.client.ProgramExecutionResultEvent;
import org.terasology.computer.event.client.ProgramListReceivedEvent;
import org.terasology.computer.event.client.ProgramTextReceivedEvent;
import org.terasology.computer.event.server.ConsoleListeningRegistrationEvent;
import org.terasology.computer.event.server.DeleteProgramEvent;
import org.terasology.computer.event.server.ExecuteProgramEvent;
import org.terasology.computer.event.server.GetProgramTextEvent;
import org.terasology.computer.event.server.ListProgramsEvent;
import org.terasology.computer.event.server.SaveProgramEvent;
import org.terasology.computer.system.server.lang.ComputerModule;
import org.terasology.computer.system.server.lang.computer.ComputerObjectDefinition;
import org.terasology.computer.system.server.lang.console.ConsoleObjectDefinition;
import org.terasology.computer.system.server.lang.os.OSObjectDefinition;
import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.entity.lifecycleEvents.BeforeDeactivateComponent;
import org.terasology.entitySystem.entity.lifecycleEvents.OnActivatedComponent;
import org.terasology.entitySystem.entity.lifecycleEvents.OnAddedComponent;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.entitySystem.systems.UpdateSubscriberSystem;
import org.terasology.logic.inventory.InventoryComponent;
import org.terasology.logic.inventory.events.BeforeItemPutInInventory;
import org.terasology.network.events.DisconnectedEvent;
import org.terasology.registry.In;
import org.terasology.registry.Share;
import org.terasology.world.block.BlockComponent;
import org.terasology.world.block.items.OnBlockItemPlaced;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

@RegisterSystem(RegisterMode.AUTHORITY)
@Share(value = ComputerModuleRegistry.class)
public class ComputerServerSystem extends BaseComponentSystem implements UpdateSubscriberSystem, ComputerModuleRegistry {
    private static final Logger logger = LoggerFactory.getLogger(ComputerServerSystem.class);

    @In
    private EntityManager entityManager;

    private EntityRef computerSystemEntity;

    private Map<Integer, ComputerContext> computerContextMap = new HashMap<>();

    private Map<String, ObjectDefinition> predefinedComputerVariables = new HashMap<>();
    private ExecutionCostConfiguration executionCostConfiguration = new SampleExecutionCostConfiguration();

    private Map<String, ComputerModule> computerModuleRegistry = new HashMap<>();

    @Override
    public void initialise() {
        predefinedComputerVariables.put("console", new ConsoleObjectDefinition());
        predefinedComputerVariables.put("os", new OSObjectDefinition());
        predefinedComputerVariables.put("computer", new ComputerObjectDefinition());
    }

    @Override
    public void postBegin() {
        Iterator<EntityRef> computerSystemEntities = entityManager.getEntitiesWith(ComputerSystemComponent.class).iterator();
        if (computerSystemEntities.hasNext()) {
            computerSystemEntity = computerSystemEntities.next();
        } else {
            computerSystemEntity = entityManager.create();
            computerSystemEntity.addComponent(new ComputerSystemComponent());
        }
    }

    @Override
    public void update(float delta) {
        for (ComputerContext computerContext : computerContextMap.values()) {
            computerContext.executeContext(delta);
        }
    }

    @Override
    public void registerComputerModule(String type, ComputerModule computerModule) {
        computerModuleRegistry.put(type, computerModule);
    }

    @Override
    public ComputerModule getComputerModuleByType(String type) {
        return computerModuleRegistry.get(type);
    }

    @ReceiveEvent
    public void computerAppearsInWorld(OnAddedComponent event, EntityRef computerEntity, BlockComponent block, ComputerComponent computer) {
        logger.debug("Computer added to world as block");
        if (computer.computerId == -1) {
            computer.computerId = assignNextId();
            logger.debug("Assigning a new ID to computer: "+computer.computerId);
            computerEntity.saveComponent(computer);
        } else {
            logger.debug("Computer has an ID already: "+computer.computerId);
        }
    }

    @ReceiveEvent
    public void computerLoadedInWorld(OnActivatedComponent event, EntityRef computerEntity, BlockComponent block, ComputerComponent computer) {
        logger.debug("Creating computer context for computer: "+computer.computerId);
        computerContextMap.put(computer.computerId, new ComputerContext(this, computerEntity, computer.cpuSpeed, computer.stackSize, computer.memorySize));
    }

    @ReceiveEvent
    public void computerUnloadedFromWorld(BeforeDeactivateComponent event, EntityRef computerEntity, BlockComponent block, ComputerComponent computer) {
        logger.debug("Destroying computer context for computer: "+computer.computerId);
        computerContextMap.remove(computer.computerId);
    }

    @ReceiveEvent
    public void computerPlacedInWorld(OnBlockItemPlaced event, EntityRef itemEntity, ComputerComponent component) {
        ComputerComponent itemComponent = itemEntity.getComponent(ComputerComponent.class);
        logger.debug("Computer placed from item, computer id: " + itemComponent.computerId);
        if (itemComponent.computerId != -1) {
            ComputerComponent blockComponent = event.getPlacedBlock().getComponent(ComputerComponent.class);
            copyValues(itemComponent, blockComponent);
            event.getPlacedBlock().saveComponent(blockComponent);
        }
    }

    private void copyValues(ComputerComponent fromComponent, ComputerComponent toComponent) {
        // Do not copy computerId, it should be new on each placement
//        toComponent.computerId = fromComponent.computerId;
        toComponent.moduleSlotStart = fromComponent.moduleSlotStart;
        toComponent.moduleSlotCount = fromComponent.moduleSlotCount;
        toComponent.cpuSpeed = fromComponent.cpuSpeed;
        toComponent.stackSize = fromComponent.stackSize;
        toComponent.memorySize = fromComponent.memorySize;
        toComponent.programs = fromComponent.programs;
    }

    @ReceiveEvent
    public void executeProgramRequested(ExecuteProgramEvent event, EntityRef client) {
        EntityRef computerEntity = event.getComputerEntity();
        ComputerComponent computer = computerEntity.getComponent(ComputerComponent.class);
        if (computer != null) {
            ComputerContext computerContext = computerContextMap.get(computer.computerId);
            if (computerContext.isRunningProgram()) {
                client.send(new ProgramExecutionResultEvent("There is a program already running on the computer"));
            } else {
                String programName = event.getProgramName();
                String programText = computer.programs.get(programName);
                if (programText != null) {
                    try {
                        computerContext.startProgram(programName, programText, predefinedComputerVariables, executionCostConfiguration);
                        client.send(new ProgramExecutionResultEvent("Program started"));
                    } catch (IllegalSyntaxException exp) {
                        client.send(new ProgramExecutionResultEvent(exp.getMessage()));
                    }
                } else {
                    client.send(new ProgramExecutionResultEvent("Program not found"));
                }
            }
        }
    }

    @ReceiveEvent
    public void saveProgramRequested(SaveProgramEvent event, EntityRef client) {
        EntityRef computerEntity = event.getComputerEntity();
        ComputerComponent computer = computerEntity.getComponent(ComputerComponent.class);
        if (computer != null) {
            computer.programs.put(event.getProgramName(), event.getProgramText());
            computerEntity.saveComponent(computer);
        }
    }

    @ReceiveEvent
    public void deleteProgramRequested(DeleteProgramEvent event, EntityRef client) {
        EntityRef computerEntity = event.getComputerEntity();
        ComputerComponent computer = computerEntity.getComponent(ComputerComponent.class);
        if (computer != null) {
            computer.programs.remove(event.getProgramName());
            computerEntity.saveComponent(computer);
        }
    }

    @ReceiveEvent
    public void programTextRequested(GetProgramTextEvent event, EntityRef client) {
        EntityRef computerEntity = event.getComputerEntity();
        ComputerComponent computer = computerEntity.getComponent(ComputerComponent.class);
        if (computer != null) {
            String programText = computer.programs.get(event.getProgramName());
            if (programText == null)
                programText = "";
            client.send(new ProgramTextReceivedEvent(event.getProgramName(), programText));
        }
    }

    @ReceiveEvent
    public void listOfProgramsRequested(ListProgramsEvent event, EntityRef client) {
        EntityRef computerEntity = event.getComputerEntity();
        ComputerComponent computer = computerEntity.getComponent(ComputerComponent.class);
        if (computer != null) {
            TreeSet<String> programNames = new TreeSet<>(computer.programs.keySet());
            client.send(new ProgramListReceivedEvent(programNames));
        }
    }

    @ReceiveEvent
    public void consoleListeningRegistrationRequested(ConsoleListeningRegistrationEvent event, EntityRef client) {
        EntityRef computerEntity = event.getComputerEntity();
        ComputerComponent computer = computerEntity.getComponent(ComputerComponent.class);
        if (computer != null) {
            ComputerContext computerContext = computerContextMap.get(computer.computerId);
            if (event.isRegister()) {
                computerContext.registerConsoleListener(client, new SendingEventsComputerConsoleListener(client));
            } else {
                computerContext.deregisterConsoleListener(client);
            }
        }
    }

    @ReceiveEvent
    public void clientDisconnected(DisconnectedEvent event, EntityRef client) {
        // TODO: Slow - just going through all running computers, we should store who is listening to what somewhere
        for (ComputerContext computerContext : computerContextMap.values()) {
            computerContext.deregisterConsoleListener(client);
        }
    }

    @ReceiveEvent
    public void validateModuleInsertion(BeforeItemPutInInventory event, EntityRef computerEntity, ComputerComponent computer) {
        int slotStart = computer.moduleSlotStart;
        int slotCount = computer.moduleSlotCount;

        int slot = event.getSlot();

        if (slotStart<=slot && slot<slotStart+slotCount) {
            ComputerModuleComponent module = event.getItem().getComponent(ComputerModuleComponent.class);
            if (module == null) {
                event.consume();
            } else {
                int moduleSlotEntered = slot-slotStart;

                InventoryComponent inventory = computerEntity.getComponent(InventoryComponent.class);
                Collection<ComputerModule> existingModules = new HashSet<>();
                for (int i=0; i<slotCount; i++) {
                    if (i != moduleSlotEntered) {
                        ComputerModuleComponent existingModule = inventory.itemSlots.get(i).getComponent(ComputerModuleComponent.class);
                        if (existingModule != null) {
                            existingModules.add(getComputerModuleByType(existingModule.moduleType));
                        }
                    }
                }

                ComputerModule computerModule = getComputerModuleByType(module.moduleType);
                if (!computerModule.canBePlacedInComputer(existingModules)) {
                    event.consume();
                }

                for (ComputerModule existingModule : existingModules) {
                    if (!existingModule.acceptsNewModule(computerModule)) {
                        event.consume();
                    }
                }
            }
        }
    }

    private int assignNextId() {
        ComputerSystemComponent computerSystem = computerSystemEntity.getComponent(ComputerSystemComponent.class);
        int result = computerSystem.maxId++;
        computerSystemEntity.saveComponent(computerSystem);
        return result;
    }
}
