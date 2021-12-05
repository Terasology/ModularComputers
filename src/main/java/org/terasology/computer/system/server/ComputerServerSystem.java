// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.computer.system.server;

import com.gempukku.lang.ExecutionCostConfiguration;
import com.gempukku.lang.IllegalSyntaxException;
import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.computer.component.ComputerComponent;
import org.terasology.computer.component.ComputerModuleComponent;
import org.terasology.computer.component.ComputerSystemComponent;
import org.terasology.computer.context.ComputerContext;
import org.terasology.computer.event.client.ForceTerminalCloseEvent;
import org.terasology.computer.event.client.ProgramExecutionResultEvent;
import org.terasology.computer.event.client.ProgramListReceivedEvent;
import org.terasology.computer.event.client.ProgramTextReceivedEvent;
import org.terasology.computer.event.server.ConsoleListeningRegistrationEvent;
import org.terasology.computer.event.server.CopyProgramEvent;
import org.terasology.computer.event.server.DeleteProgramEvent;
import org.terasology.computer.event.server.ExecuteProgramEvent;
import org.terasology.computer.event.server.GetProgramTextEvent;
import org.terasology.computer.event.server.ListProgramsEvent;
import org.terasology.computer.event.server.RenameProgramEvent;
import org.terasology.computer.event.server.SaveProgramEvent;
import org.terasology.computer.event.server.StopProgramEvent;
import org.terasology.computer.system.common.ComputerLanguageContextInitializer;
import org.terasology.computer.system.common.ComputerModuleRegistry;
import org.terasology.computer.system.server.lang.ComputerModule;
import org.terasology.engine.entitySystem.entity.EntityManager;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.entity.lifecycleEvents.BeforeDeactivateComponent;
import org.terasology.engine.entitySystem.entity.lifecycleEvents.OnActivatedComponent;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterMode;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.entitySystem.systems.UpdateSubscriberSystem;
import org.terasology.engine.logic.characters.CharacterComponent;
import org.terasology.engine.logic.config.ModuleConfigManager;
import org.terasology.gestalt.entitysystem.event.ReceiveEvent;
import org.terasology.module.inventory.components.InventoryComponent;
import org.terasology.module.inventory.systems.InventoryManager;
import org.terasology.module.inventory.systems.InventoryUtils;
import org.terasology.module.inventory.events.BeforeItemPutInInventory;
import org.terasology.engine.logic.location.LocationComponent;
import org.terasology.engine.network.ClientComponent;
import org.terasology.engine.network.events.DisconnectedEvent;
import org.terasology.engine.registry.In;
import org.terasology.engine.world.block.BlockComponent;
import org.terasology.engine.world.block.items.OnBlockItemPlaced;
import org.terasology.gestalt.entitysystem.event.ReceiveEvent;
import org.terasology.mobileBlocks.server.AfterBlockMovedEvent;
import org.terasology.mobileBlocks.server.BeforeBlockMovesEvent;
import org.terasology.mobileBlocks.server.BlockTransitionDuringMoveEvent;
import org.terasology.module.inventory.components.InventoryComponent;
import org.terasology.module.inventory.events.BeforeItemPutInInventory;
import org.terasology.module.inventory.systems.InventoryManager;
import org.terasology.module.inventory.systems.InventoryUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

@RegisterSystem(RegisterMode.AUTHORITY)
public class ComputerServerSystem extends BaseComponentSystem implements UpdateSubscriberSystem {
    private static final Logger logger = LoggerFactory.getLogger(ComputerServerSystem.class);
    private static final float TERMINAL_MAX_DISTANCE = 10;

    @In
    private EntityManager entityManager;
    @In
    private InventoryManager inventoryManager;
    @In
    private ComputerModuleRegistry computerModuleRegistry;
    @In
    private ComputerLanguageContextInitializer computerLanguageContextInitializer;
    @In
    private ModuleConfigManager moduleConfigManager;

    private EntityRef computerSystemEntity;

    private Map<Integer, ComputerContext> computerContextMap = new HashMap<>();

    private ExecutionCostConfiguration executionCostConfiguration;

    private boolean computerInTransitionState;

    @Override
    public void postBegin() {
        Iterator<EntityRef> computerSystemEntities = entityManager.getEntitiesWith(ComputerSystemComponent.class).iterator();
        if (computerSystemEntities.hasNext()) {
            computerSystemEntity = computerSystemEntities.next();
        } else {
            computerSystemEntity = entityManager.create();
            computerSystemEntity.addComponent(new ComputerSystemComponent());
        }

        executionCostConfiguration = createExecutionCostConfiguration();
    }

    private ExecutionCostConfiguration createExecutionCostConfiguration() {
        ConfigurableExecutionCostConfiguration result = new ConfigurableExecutionCostConfiguration();
        result.setGetContextValue(getConfigValue("getContextValue", 1));
        result.setSetContextValue(getConfigValue("setContextValue", 1));
        result.setGetReturnValue(getConfigValue("getReturnValue", 1));
        result.setSetReturnValue(getConfigValue("setReturnValue", 1));
        result.setBreakBlock(getConfigValue("breakBlock", 2));
        result.setDefineVariable(getConfigValue("defineVariable", 2));
        result.setSetVariable(getConfigValue("setVariable", 2));
        result.setStackExecution(getConfigValue("stackExecution", 5));
        result.setStackGroupExecution(getConfigValue("stackGroupExecution", 8));
        result.setSumValues(getConfigValue("sumValues", 10));
        result.setOtherMathOperation(getConfigValue("otherMathOperation", 10));
        result.setCompareValues(getConfigValue("compareValues", 10));
        result.setResolveMember(getConfigValue("resolveMember", 10));
        return result;
    }

    private int getConfigValue(String name, int defaultValue) {
        return moduleConfigManager.getIntVariable("ModularComputers", "executionCost." + name, defaultValue);
    }

    @Override
    public void update(float delta) {
        for (ComputerContext computerContext : computerContextMap.values()) {
            computerContext.executeContext();

            Vector3f computerLocation = computerContext.getComputerCallback().getComputerLocation();

            ComputerComponent computer = computerContext.getEntity().getComponent(ComputerComponent.class);

            // Validate computer distance from listening clients
            for (EntityRef listeningClient : computerContext.getConsoleListenerMap().keySet()) {
                if (!validateComputerToCharacterDistance(listeningClient, computerLocation)) {
                    computerContext.deregisterConsoleListener(listeningClient);
                    listeningClient.send(new ForceTerminalCloseEvent(computer.computerId));
                }
            }
        }
    }

    private boolean validateComputerToCharacterDistance(EntityRef character, ComputerContext computerContext) {
        Vector3f computerLocation = computerContext.getComputerCallback().getComputerLocation();
        return validateComputerToCharacterDistance(character, computerLocation);
    }

    private boolean validateComputerToCharacterDistance(EntityRef character, Vector3f computerLocation) {
        Vector3f clientLocation = getClientLocation(character);
        return clientLocation != null && clientLocation.distance(computerLocation) <= TERMINAL_MAX_DISTANCE;
    }

    private Vector3f getClientLocation(EntityRef listeningClient) {
        LocationComponent location = listeningClient.getComponent(LocationComponent.class);
        if (location == null) {
            return null;
        }
        return location.getWorldPosition(new Vector3f());
    }

    @ReceiveEvent
    public void computerLoadedInWorld(OnActivatedComponent event, EntityRef computerEntity,
                                      BlockComponent block, ComputerComponent computer) {
        if (!computerInTransitionState) {
            if (computer.computerId == -1) {
                computer.computerId = assignNextId();
                logger.debug("Assigning a new ID to computer: " + computer.computerId);
                computerEntity.saveComponent(computer);
            }
            logger.debug("Creating computer context for computer: " + computer.computerId);
            computerContextMap.put(computer.computerId, new ComputerContext(computerModuleRegistry, computerEntity, computer.cpuSpeed,
                    computer.stackSize, computer.memorySize));
        }
    }

    @ReceiveEvent
    public void computerUnloadedFromWorld(BeforeDeactivateComponent event, EntityRef computerEntity,
                                          BlockComponent block, ComputerComponent computer) {
        if (!computerInTransitionState) {
            logger.debug("Destroying computer context for computer: " + computer.computerId);
            computerContextMap.remove(computer.computerId);
        }
    }

    @ReceiveEvent
    public void computerPlacedInWorld(OnBlockItemPlaced event, EntityRef itemEntity, ComputerComponent component) {
        ComputerComponent itemComponent = itemEntity.getComponent(ComputerComponent.class);
        logger.debug("Computer placed from item, computer id: " + itemComponent.computerId);
        if (itemComponent.computerId != -1) {
            ComputerComponent blockComponent = event.getPlacedBlock().getComponent(ComputerComponent.class);
            copyValues(itemComponent, blockComponent, false);
            event.getPlacedBlock().saveComponent(blockComponent);
        }
    }

    @ReceiveEvent
    public void beforeComputerMoveSetTransitionState(BeforeBlockMovesEvent event, EntityRef entity, ComputerComponent computer) {
        computerInTransitionState = true;
    }

    @ReceiveEvent
    public void computerMovedCopyInventory(BlockTransitionDuringMoveEvent event, EntityRef entity, ComputerComponent computer) {
        EntityRef newEntity = event.getIntoEntity();

        ComputerComponent newEntityComponent = newEntity.getComponent(ComputerComponent.class);
        copyValues(computer, newEntityComponent, true);
        newEntity.saveComponent(newEntityComponent);

        int slotCount = InventoryUtils.getSlotCount(entity);
        // We assume the number of slots does not change
        for (int i = 0; i < slotCount; i++) {
            // We assume that modules are not stackable, so only one goes in
            inventoryManager.moveItem(entity, null, i, newEntity, i, 1);
        }

        // Update context with the new computer entity
        ComputerContext context = computerContextMap.get(computer.computerId);
        context.updateComputerEntity(newEntity);
    }

    @ReceiveEvent
    public void afterComputerMoveSetTransitionState(AfterBlockMovedEvent event, EntityRef entity, ComputerComponent computer) {
        computerInTransitionState = false;
    }

    private void copyValues(ComputerComponent fromComponent, ComputerComponent toComponent, boolean copyComputerId) {
        if (copyComputerId) {
            toComponent.computerId = fromComponent.computerId;
        }
        toComponent.moduleSlotStart = fromComponent.moduleSlotStart;
        toComponent.moduleSlotCount = fromComponent.moduleSlotCount;
        toComponent.cpuSpeed = fromComponent.cpuSpeed;
        toComponent.stackSize = fromComponent.stackSize;
        toComponent.memorySize = fromComponent.memorySize;
        toComponent.programs = fromComponent.programs;
    }

    @ReceiveEvent
    public void executeProgramRequested(ExecuteProgramEvent event, EntityRef client) {
        ComputerContext computerContext = computerContextMap.get(event.getComputerId());
        if (computerContext != null && validateComputerToCharacterDistance(client, computerContext)) {
            EntityRef clientInfo = client.getComponent(CharacterComponent.class).controller.getComponent(ClientComponent.class).clientInfo;
            ComputerComponent computer = computerContext.getEntity().getComponent(ComputerComponent.class);
            if (computerContext.isRunningProgram()) {
                client.send(new ProgramExecutionResultEvent(computer.computerId,
                        "There is a program already running on the computer"));
            } else {
                String programName = event.getProgramName();
                String programText = computer.programs.get(programName);
                if (programText != null) {
                    try {
                        computerContext.startProgram(programName, clientInfo, programText, event.getParams(),
                                computerLanguageContextInitializer, executionCostConfiguration);
                        client.send(new ProgramExecutionResultEvent(computer.computerId,
                                "Program started"));
                    } catch (IllegalSyntaxException exp) {
                        client.send(new ProgramExecutionResultEvent(computer.computerId,
                                exp.getMessage()));
                    }
                } else {
                    client.send(new ProgramExecutionResultEvent(computer.computerId,
                            "Program not found"));
                }
            }
        }
    }

    @ReceiveEvent
    public void saveProgramRequested(SaveProgramEvent event, EntityRef client) {
        ComputerContext computerContext = computerContextMap.get(event.getComputerId());
        if (computerContext != null && validateComputerToCharacterDistance(client, computerContext)) {
            EntityRef computerEntity = computerContext.getEntity();
            ComputerComponent computer = computerEntity.getComponent(ComputerComponent.class);
            computer.programs.put(event.getProgramName(), event.getProgramText());
            computerEntity.saveComponent(computer);
        }
    }

    @ReceiveEvent
    public void deleteProgramRequested(DeleteProgramEvent event, EntityRef client) {
        ComputerContext computerContext = computerContextMap.get(event.getComputerId());
        if (computerContext != null && validateComputerToCharacterDistance(client, computerContext)) {
            EntityRef computerEntity = computerContext.getEntity();
            ComputerComponent computer = computerEntity.getComponent(ComputerComponent.class);
            computer.programs.remove(event.getProgramName());
            computerEntity.saveComponent(computer);
        }
    }

    @ReceiveEvent
    public void copyProgramRequested(CopyProgramEvent event, EntityRef client) {
        ComputerContext computerContext = computerContextMap.get(event.getComputerId());
        if (computerContext != null && validateComputerToCharacterDistance(client, computerContext)) {
            EntityRef computerEntity = computerContext.getEntity();
            ComputerComponent computer = computerEntity.getComponent(ComputerComponent.class);
            String programText = computer.programs.get(event.getProgramNameSource());
            if (programText != null) {
                computer.programs.put(event.getProgramNameDestination(), programText);
            }
            computerEntity.saveComponent(computer);
        }
    }

    @ReceiveEvent
    public void renameProgramRequested(RenameProgramEvent event, EntityRef client) {
        ComputerContext computerContext = computerContextMap.get(event.getComputerId());
        if (computerContext != null && validateComputerToCharacterDistance(client, computerContext)) {
            EntityRef computerEntity = computerContext.getEntity();
            ComputerComponent computer = computerEntity.getComponent(ComputerComponent.class);
            String programText = computer.programs.remove(event.getProgramNameOld());
            if (programText != null) {
                computer.programs.put(event.getProgramNameNew(), programText);
            }
            computerEntity.saveComponent(computer);
        }
    }

    @ReceiveEvent
    public void programTextRequested(GetProgramTextEvent event, EntityRef client) {
        ComputerContext computerContext = computerContextMap.get(event.getComputerId());
        if (computerContext != null && validateComputerToCharacterDistance(client, computerContext)) {
            ComputerComponent computer = computerContext.getEntity().getComponent(ComputerComponent.class);
            String programText = computer.programs.get(event.getProgramName());
            if (programText == null) {
                programText = "";
            }
            client.send(new ProgramTextReceivedEvent(computer.computerId, event.getProgramName(), programText));
        }
    }

    @ReceiveEvent
    public void listOfProgramsRequested(ListProgramsEvent event, EntityRef client) {
        ComputerContext computerContext = computerContextMap.get(event.getComputerId());
        if (computerContext != null && validateComputerToCharacterDistance(client, computerContext)) {
            EntityRef computerEntity = computerContext.getEntity();
            ComputerComponent computer = computerEntity.getComponent(ComputerComponent.class);
            Set<String> programNames = new TreeSet<>(computer.programs.keySet());
            client.send(new ProgramListReceivedEvent(computer.computerId, programNames));
        }
    }

    @ReceiveEvent
    public void stopProgramRequested(StopProgramEvent event, EntityRef client) {
        ComputerContext computerContext = computerContextMap.get(event.getComputerId());
        if (computerContext != null && validateComputerToCharacterDistance(client, computerContext)) {
            computerContext.stopProgram();
        }
    }

    @ReceiveEvent
    public void consoleListeningRegistrationRequested(ConsoleListeningRegistrationEvent event, EntityRef client) {
        ComputerContext computerContext = computerContextMap.get(event.getComputerId());
        if (computerContext != null && validateComputerToCharacterDistance(client, computerContext)) {
            ComputerComponent computer = computerContext.getEntity().getComponent(ComputerComponent.class);
            if (event.isRegister()) {
                computerContext.registerConsoleListener(client, new SendingEventsComputerConsoleListener(computer.computerId, client));
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

        if (slotStart <= slot && slot < slotStart + slotCount) {
            ComputerModuleComponent module = event.getItem().getComponent(ComputerModuleComponent.class);
            if (module == null) {
                event.consume();
            } else {
                int moduleSlotEntered = slot - slotStart;

                InventoryComponent inventory = computerEntity.getComponent(InventoryComponent.class);
                Collection<ComputerModule> existingModules = new HashSet<>();
                for (int i = 0; i < slotCount; i++) {
                    if (i != moduleSlotEntered) {
                        ComputerModuleComponent existingModule = inventory.itemSlots.get(i).getComponent(ComputerModuleComponent.class);
                        if (existingModule != null) {
                            existingModules.add(computerModuleRegistry.getComputerModuleByType(existingModule.moduleType));
                        }
                    }
                }

                ComputerModule computerModule = computerModuleRegistry.getComputerModuleByType(module.moduleType);
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
