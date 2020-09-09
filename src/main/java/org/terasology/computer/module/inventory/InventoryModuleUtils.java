// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.computer.module.inventory;

import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.logic.common.DisplayNameComponent;
import org.terasology.engine.logic.inventory.ItemComponent;

public class InventoryModuleUtils {
    private InventoryModuleUtils() {
    }

    public static int getItemCount(EntityRef itemEntity) {
        ItemComponent item = itemEntity.getComponent(ItemComponent.class);
        if (item == null)
            return 0;

        return item.stackCount;
    }

    public static String getItemName(EntityRef itemEntity) {
        ItemComponent item = itemEntity.getComponent(ItemComponent.class);
        if (item == null)
            return null;

        DisplayNameComponent displayName = itemEntity.getComponent(DisplayNameComponent.class);
        if (displayName == null)
            return "Unknown";

        return displayName.name;
    }
}
