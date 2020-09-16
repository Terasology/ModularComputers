// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.modularcomputers.module.mobility;

import org.terasology.engine.math.Direction;
import org.terasology.math.geom.Vector3f;
import org.terasology.math.geom.Vector3i;
import org.terasology.mobileBlocks.server.BlockMoveManager;
import org.terasology.modularcomputers.FunctionParamValidationUtil;
import org.terasology.modularcomputers.context.ComputerCallback;
import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.ExecutionException;
import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.Variable;
import org.terasology.modularcomputers.system.server.lang.AbstractModuleMethodExecutable;

import java.util.Map;

public class MoveMethod extends AbstractModuleMethodExecutable<Boolean> {
    private static final int MOVE_TIME = 500;
    private final String methodName;

    private final BlockMoveManager blockMoveManager;

    public MoveMethod(String methodName, BlockMoveManager blockMoveManager) {
        super("Moves the computer in the specified direction (if able).", "Boolean", "If the movement was successful.");
        this.blockMoveManager = blockMoveManager;
        this.methodName = methodName;

        addParameter("direction", "Direction", "Specifies the direction in which the computer should move.");

        addExample("This example makes the computer move up one block. Please make sure " +
                        "this computer has a module of Mobility type in any of its slots.",
                "var mobilityMod = computer.bindModuleOfType(\"" + MobilityModuleCommonSystem.MOBILITY_MODULE_TYPE + 
                        "\");\n" +
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

        Direction direction = FunctionParamValidationUtil.validateDirectionParameter(line, parameters, "direction",
                methodName);

        Vector3f computerLocation = computer.getComputerLocation();

        return blockMoveManager.moveBlock(new Vector3i(computerLocation), direction, MOVE_TIME);
    }

    @Override
    public Object onFunctionEnd(int line, ComputerCallback computer, Map<String, Variable> parameters,
                                Boolean moveResult) throws ExecutionException {
        return moveResult;
    }

}
