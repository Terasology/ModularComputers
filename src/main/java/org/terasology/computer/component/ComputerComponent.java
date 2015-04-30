/*
 * Copyright 2015 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.terasology.computer.component;

import org.terasology.entitySystem.Component;
import org.terasology.network.Replicate;
import org.terasology.world.block.ForceBlockActive;
import org.terasology.world.block.items.AddToBlockBasedItem;

import java.util.HashMap;
import java.util.Map;

@ForceBlockActive
@AddToBlockBasedItem
public class ComputerComponent implements Component {
    public Map<String, String> programs = new HashMap<>();
    public int moduleSlotStart = 0;
    public int moduleSlotCount = 4;
    public int cpuSpeed = 10;
    public int stackSize = 10;
    public int memorySize = 1024;
    @Replicate
    public int computerId = -1;
}
