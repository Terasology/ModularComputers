// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.computer.component;

import org.terasology.gestalt.entitysystem.component.Component;

public class ComputerModuleComponent implements Component<ComputerModuleComponent> {
    public String moduleType;

    @Override
    public void copyFrom(ComputerModuleComponent other) {
        this.moduleType = other.moduleType;
    }
}
