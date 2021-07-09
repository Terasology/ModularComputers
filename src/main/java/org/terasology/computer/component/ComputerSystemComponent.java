// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.computer.component;

import org.terasology.gestalt.entitysystem.component.Component;

public class ComputerSystemComponent implements Component<ComputerSystemComponent> {
    public int maxId;

    @Override
    public void copy(ComputerSystemComponent other) {
        this.maxId = other.maxId;
    }
}
