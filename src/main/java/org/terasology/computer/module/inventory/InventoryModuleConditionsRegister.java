// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.computer.module.inventory;

import org.terasology.computer.system.server.lang.os.condition.InventoryCondition;
import org.terasology.engine.entitySystem.entity.EntityRef;

public interface InventoryModuleConditionsRegister {
    void addInventoryChangeListener(EntityRef entity, InventoryCondition latchCondition);

    void removeInventoryChangeListener(EntityRef entity, InventoryCondition latchCondition);
}
