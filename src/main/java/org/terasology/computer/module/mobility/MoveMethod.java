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
import org.terasology.computer.system.server.lang.ModuleMethodExecutable;
import org.terasology.math.Direction;
import org.terasology.math.geom.Vector3f;
import org.terasology.math.geom.Vector3i;
import org.terasology.world.block.move.server.BlockMoveManager;

import java.util.Map;

public class MoveMethod implements ModuleMethodExecutable<Boolean> {
    private static final Logger logger = LoggerFactory.getLogger(MoveMethod.class);
    private static final int MOVE_TIME = 500;

    private BlockMoveManager blockMoveManager;

    public MoveMethod(BlockMoveManager blockMoveManager) {
        this.blockMoveManager = blockMoveManager;
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
    public Boolean onFunctionStart(int line, ComputerCallback computer, Map<String, Variable> parameters) throws ExecutionException {

        Direction direction = FunctionParamValidationUtil.validateDirectionParameter(line, parameters, "direction", "move");

        Vector3f computerLocation = computer.getComputerLocation();

        return blockMoveManager.moveBlock(new Vector3i(computerLocation), direction, MOVE_TIME);
    }

    @Override
    public Object onFunctionEnd(int line, ComputerCallback computer, Map<String, Variable> parameters, Boolean moveResult) throws ExecutionException {
        return moveResult;
    }

}
