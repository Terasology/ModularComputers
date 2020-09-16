// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.modularcomputers.module.inventory;

import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.modularcomputers.system.server.lang.os.condition.InventoryCondition;

public interface InventoryModuleConditionsRegister {
    void addInventoryChangeListener(EntityRef entity, InventoryCondition latchCondition);

    void removeInventoryChangeListener(EntityRef entity, InventoryCondition latchCondition);
}
