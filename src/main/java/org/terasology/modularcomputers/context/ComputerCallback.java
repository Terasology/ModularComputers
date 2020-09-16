// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.modularcomputers.context;

import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.math.geom.Vector3f;
import org.terasology.modularcomputers.system.server.lang.ComputerModule;
import org.terasology.modularcomputers.system.server.lang.os.condition.ResultAwaitingCondition;

public interface ComputerCallback {
    ComputerConsole getConsole();

    int getModuleSlotsCount();

    ComputerModule getModule(int slot);

    Vector3f getComputerLocation();

    void suspendWithCondition(ResultAwaitingCondition condition);

    EntityRef getComputerEntity();

    EntityRef getExecutedBy();
}
