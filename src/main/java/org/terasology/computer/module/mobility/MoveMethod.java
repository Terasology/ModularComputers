// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.computer.module.mobility;

import com.gempukku.lang.ExecutionException;
import com.gempukku.lang.Variable;
import org.joml.RoundingMode;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.terasology.computer.FunctionParamValidationUtil;
import org.terasology.computer.context.ComputerCallback;
import org.terasology.computer.system.server.lang.AbstractModuleMethodExecutable;
import org.terasology.engine.math.Direction;
import org.terasology.mobileBlocks.server.BlockMoveManager;

import java.util.Map;

public class MoveMethod extends AbstractModuleMethodExecutable<Boolean> {
    private static final int MOVE_TIME = 500;
    private final String methodName;

    private BlockMoveManager blockMoveManager;

    public MoveMethod(String methodName, BlockMoveManager blockMoveManager) {
        super("Moves the computer in the specified direction (if able).", "Boolean", "If the movement was successful.");
        this.blockMoveManager = blockMoveManager;
        this.methodName = methodName;

        addParameter("direction", "Direction", "Specifies the direction in which the computer should move.");

        addExample("This example makes the computer move up one block. Please make sure " +
                        "this computer has a module of Mobility type in any of its slots.",
                "var mobilityMod = computer.bindModuleOfType(\"" + MobilityModuleCommonSystem.MOBILITY_MODULE_TYPE + "\");\n" +
                        "mobilityMod.move(\"up\");");
    }

    @Override
    public int getCpuCycleDuration() {
        return 100;
    }

    @Override
    public int getMinimumExecutionTime(int line, ComputerCallback computer, Map<String, Variable> parameters) throws ExecutionException {
        return MOVE_TIME;
    }

    @Override
    public Boolean onFunctionStart(int line, ComputerCallback computer, Map<String, Variable> parameters) throws ExecutionException {

        Direction direction = FunctionParamValidationUtil.validateDirectionParameter(line, parameters, "direction", methodName);

        Vector3f computerLocation = computer.getComputerLocation();

        return blockMoveManager.moveBlock(new Vector3i(computerLocation, RoundingMode.FLOOR), direction, MOVE_TIME);
    }

    @Override
    public Object onFunctionEnd(int line, ComputerCallback computer, Map<String, Variable> parameters, Boolean moveResult) throws ExecutionException {
        return moveResult;
    }

}
