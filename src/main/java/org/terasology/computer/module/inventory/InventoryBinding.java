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
package org.terasology.computer.module.inventory;

import com.gempukku.lang.ExecutionException;
import org.terasology.computer.context.ComputerCallback;
import org.terasology.entitySystem.entity.EntityRef;

import java.util.List;

public interface InventoryBinding {
    public InventoryWithSlots getInventoryEntity(int line, ComputerCallback computerCallback) throws ExecutionException;

    public boolean isInput();

    public class InventoryWithSlots {
        public final EntityRef inventory;
        public final List<Integer> slots;

        public InventoryWithSlots(EntityRef inventory, List<Integer> slots) {
            this.inventory = inventory;
            this.slots = slots;
        }
    }
}
