// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.computer.component;

import org.terasology.engine.entitySystem.Component;
import org.terasology.engine.network.Replicate;
import org.terasology.engine.world.block.ForceBlockActive;
import org.terasology.engine.world.block.items.AddToBlockBasedItem;

import java.util.HashMap;
import java.util.Map;

@ForceBlockActive
@AddToBlockBasedItem
public class ComputerComponent implements Component {
    public Map<String, String> programs = new HashMap<>();
    public int moduleSlotStart;
    public int moduleSlotCount = 4;
    public int cpuSpeed = 10;
    public int stackSize = 10;
    public int memorySize = 1024;
    @Replicate
    public int computerId = -1;
}
