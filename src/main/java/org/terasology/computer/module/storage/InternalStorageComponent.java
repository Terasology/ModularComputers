// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.computer.module.storage;

import org.terasology.engine.entitySystem.Owns;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.gestalt.entitysystem.component.Component;

public class InternalStorageComponent implements Component<InternalStorageComponent> {
    @Owns
    public EntityRef inventoryEntity;

    @Override
    public void copy(InternalStorageComponent other) {
        this.inventoryEntity = other.inventoryEntity;
    }
}
