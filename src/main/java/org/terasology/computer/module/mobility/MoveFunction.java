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
import org.terasology.computer.event.server.AfterComputerMoveEvent;
import org.terasology.computer.event.server.BeforeComputerMoveEvent;
import org.terasology.computer.event.server.ComputerMoveEvent;
import org.terasology.computer.system.server.lang.ModuleFunctionExecutable;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.math.Direction;
import org.terasology.math.geom.Vector3i;
import org.terasology.world.BlockEntityRegistry;
import org.terasology.world.WorldProvider;
import org.terasology.world.block.Block;
import org.terasology.world.block.BlockManager;
import org.terasology.world.block.entity.placement.PlaceBlocks;

import java.util.Map;

public class MoveFunction implements ModuleFunctionExecutable {
    private static final Logger logger = LoggerFactory.getLogger(MoveFunction.class);

    private WorldProvider worldProvider;
    private BlockEntityRegistry blockEntityRegistry;

    public MoveFunction(WorldProvider worldProvider, BlockEntityRegistry blockEntityRegistry) {
        this.worldProvider = worldProvider;
        this.blockEntityRegistry = blockEntityRegistry;
    }

    @Override
    public int getCpuCycleDuration() {
        return 100;
    }

    @Override
    public int getMinimumExecutionTicks() {
        return 20;
    }

    @Override
    public String[] getParameterNames() {
        return new String[] {"direction"};
    }

    @Override
    public Object executeFunction(int line, ComputerCallback computer, Map<String, Variable> parameters) throws ExecutionException {
        Direction direction = FunctionParamValidationUtil.validateDirectionParameter(line, parameters, "direction", "move");

        Vector3i computerLocation = computer.getComputerLocation();
        Vector3i directionVector = direction.getVector3i();
        Vector3i moveLocation = new Vector3i(
                computerLocation.x + directionVector.x,
                computerLocation.y + directionVector.y,
                computerLocation.z + directionVector.z);

        Block blockAtLocation = worldProvider.getBlock(moveLocation);
        if (blockAtLocation.isReplacementAllowed()) {
            EntityRef computerEntity = blockEntityRegistry.getBlockEntityAt(computerLocation);

            computerEntity.send(new BeforeComputerMoveEvent());
            EntityRef endingEntity = computerEntity;
            try {
                Block computerBlock = worldProvider.getBlock(computerLocation);
                PlaceBlocks placeComputerBlock = new PlaceBlocks(moveLocation, computerBlock);
                worldProvider.getWorldEntity().send(placeComputerBlock);
                if (placeComputerBlock.isConsumed()) {
                    return false;
                }

                EntityRef blockEntityAt = blockEntityRegistry.getBlockEntityAt(moveLocation);
                computerEntity.send(new ComputerMoveEvent(blockEntityAt));

                PlaceBlocks removeComputerBlock = new PlaceBlocks(computerLocation, BlockManager.getAir());
                worldProvider.getWorldEntity().send(removeComputerBlock);
                // We can't do anything at this point if this fails, so just ignore the result
                endingEntity = blockEntityAt;
            } finally {
                endingEntity.send(new AfterComputerMoveEvent());
            }

            return true;
        } else {
            return false;
        }
    }

}
