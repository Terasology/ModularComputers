// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.computer.component;

import com.google.common.collect.Maps;
import org.terasology.engine.network.Replicate;
import org.terasology.engine.world.block.ForceBlockActive;
import org.terasology.engine.world.block.items.AddToBlockBasedItem;
import org.terasology.gestalt.entitysystem.component.Component;

import java.util.HashMap;
import java.util.Map;

@ForceBlockActive
@AddToBlockBasedItem
public class ComputerComponent implements Component<ComputerComponent> {
    public Map<String, String> programs = new HashMap<>();
    public int moduleSlotStart;
    public int moduleSlotCount = 4;
    public int cpuSpeed = 10;
    public int stackSize = 10;
    public int memorySize = 1024;
    @Replicate
    public int computerId = -1;

    @Override
    public void copy(ComputerComponent other) {
        this.programs = Maps.newHashMap(other.programs);
        this.moduleSlotStart = other.moduleSlotStart;
        this.moduleSlotCount = other.moduleSlotCount;
        this.cpuSpeed = other.cpuSpeed;
        this.stackSize = other.stackSize;
        this.memorySize = other.memorySize;
        this.computerId = other.computerId;
    }
}
