// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.computer.context;

import org.joml.Vector3f;
import org.terasology.computer.system.server.lang.ComputerModule;
import org.terasology.computer.system.server.lang.os.condition.ResultAwaitingCondition;
import org.terasology.engine.entitySystem.entity.EntityRef;

public interface ComputerCallback {
    ComputerConsole getConsole();

    int getModuleSlotsCount();

    ComputerModule getModule(int slot);

    Vector3f getComputerLocation();

    void suspendWithCondition(ResultAwaitingCondition condition);

    EntityRef getComputerEntity();

    EntityRef getExecutedBy();
}
