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

import com.gempukku.lang.ExecutionException;
import com.gempukku.lang.Variable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.computer.FunctionParamValidationUtil;
import org.terasology.computer.context.ComputerCallback;
import org.terasology.computer.event.server.move.AfterComputerMoveEvent;
import org.terasology.computer.event.server.move.BeforeComputerMoveEvent;
import org.terasology.computer.event.server.move.ComputerMoveEvent;
import org.terasology.computer.system.server.lang.ModuleMethodExecutable;
import org.terasology.engine.Time;
import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.prefab.Prefab;
import org.terasology.logic.location.LocationComponent;
import org.terasology.math.Direction;
import org.terasology.math.geom.Vector3f;
import org.terasology.math.geom.Vector3i;
import org.terasology.movingBlock.MovingBlockComponent;
import org.terasology.world.BlockEntityRegistry;
import org.terasology.world.WorldProvider;
import org.terasology.world.block.Block;
import org.terasology.world.block.BlockManager;
import org.terasology.world.block.entity.placement.PlaceBlocks;

import java.util.HashMap;
import java.util.Map;

public class MoveMethod implements ModuleMethodExecutable<EntityRef> {
    private static final Logger logger = LoggerFactory.getLogger(MoveMethod.class);
    private static final int MOVE_TIME = 500;

    private WorldProvider worldProvider;
    private EntityManager entityManager;
    private BlockManager blockManager;
    private BlockEntityRegistry blockEntityRegistry;
    private EntityRef replacingInstigator;
    private Time time;

    public MoveMethod(WorldProvider worldProvider, EntityManager entityManager, BlockManager blockManager, BlockEntityRegistry blockEntityRegistry, EntityRef replacingInstigator, Time time) {
        this.worldProvider = worldProvider;
        this.entityManager = entityManager;
        this.blockManager = blockManager;
        this.blockEntityRegistry = blockEntityRegistry;
        this.replacingInstigator = replacingInstigator;
        this.time = time;
    }

    @Override
    public int getCpuCycleDuration() {
        return 100;
    }

    @Override
    public int getMinimumExecutionTime() {
        return MOVE_TIME;
    }

    @Override
    public String[] getParameterNames() {
        return new String[]{"direction"};
    }

    @Override
    public EntityRef onFunctionStart(int line, ComputerCallback computer, Map<String, Variable> parameters) throws ExecutionException {
        Direction direction = FunctionParamValidationUtil.validateDirectionParameter(line, parameters, "direction", "move");

        Vector3f computerLocation = computer.getComputerLocation();
        Vector3i directionVector = direction.getVector3i();
        Vector3i moveLocation = new Vector3i(
                computerLocation.x + directionVector.x,
                computerLocation.y + directionVector.y,
                computerLocation.z + directionVector.z);

        Block blockAtLocation = worldProvider.getBlock(moveLocation);
        if (blockAtLocation.isReplacementAllowed()) {
            long gameTime = time.getGameTimeInMs();

            Block invisibleBlock = blockManager.getBlock("ModularComputers:MovingBlockReplacement");

            EntityRef computerEntity = computer.getComputerEntity();
            computerEntity.send(new BeforeComputerMoveEvent());
            EntityRef endingEntity = computerEntity;

            Block computerBlock = worldProvider.getBlock(computerLocation);

            Prefab prefab = computerEntity.getParentPrefab();

            boolean success = false;
            EntityRef movingEntity = entityManager.create(prefab);
            try {
                movingEntity.addComponent(new LocationComponent(computerLocation));
                movingEntity.addComponent(new MovingBlockComponent(computerBlock, new Vector3i(computerLocation), moveLocation, gameTime, gameTime + MOVE_TIME));
                computerEntity.send(new ComputerMoveEvent(movingEntity));

                Map<Vector3i, Block> blocksToPlace = new HashMap<>();
                blocksToPlace.put(new Vector3i(computerLocation), invisibleBlock);
                blocksToPlace.put(moveLocation, invisibleBlock);

                PlaceBlocks placeInvisibleBlocks = new PlaceBlocks(blocksToPlace, replacingInstigator);
                try {
                    // Replace blocks with invisible ones
                    worldProvider.getWorldEntity().send(placeInvisibleBlocks);
                    if (placeInvisibleBlocks.isConsumed()) {
                        return null;
                    }

                    endingEntity = movingEntity;
                    success = true;
                    return movingEntity;
                } finally {
                    endingEntity.send(new AfterComputerMoveEvent());
                }
            } finally {
                if (!success) {
                    movingEntity.destroy();
                }
            }
        }
        return null;
    }

    @Override
    public Object onFunctionEnd(int line, ComputerCallback computer, Map<String, Variable> parameters, EntityRef movingComputerEntity) throws ExecutionException {
        if (movingComputerEntity != null) {
            MovingBlockComponent movingBlock = movingComputerEntity.getComponent(MovingBlockComponent.class);
            Vector3i locationFrom = movingBlock.getLocationFrom();
            Vector3i locationTo = movingBlock.getLocationTo();

            Map<Vector3i, Block> blocksToPlace = new HashMap<>();
            blocksToPlace.put(locationFrom, BlockManager.getAir());
            blocksToPlace.put(locationTo, movingBlock.getBlockToRender());

            movingComputerEntity.send(new BeforeComputerMoveEvent());
            EntityRef endingEntity = movingComputerEntity;
            try {
                PlaceBlocks placeBlocks = new PlaceBlocks(blocksToPlace, replacingInstigator);
                worldProvider.getWorldEntity().send(placeBlocks);

                EntityRef newComputerEntity = blockEntityRegistry.getBlockEntityAt(locationTo);
                movingComputerEntity.send(new ComputerMoveEvent(newComputerEntity));
                endingEntity = newComputerEntity;
                movingComputerEntity.destroy();
            } finally {
                endingEntity.send(new AfterComputerMoveEvent());
            }

            return true;
        }
        return false;
    }

}
