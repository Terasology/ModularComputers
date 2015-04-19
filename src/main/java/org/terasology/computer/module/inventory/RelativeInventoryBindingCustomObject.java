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

import com.gempukku.lang.CustomObject;
import com.gempukku.lang.ExecutionException;
import org.terasology.computer.context.ComputerCallback;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.logic.inventory.InventoryAccessComponent;
import org.terasology.logic.inventory.InventoryComponent;
import org.terasology.math.Direction;
import org.terasology.math.IntegerRange;
import org.terasology.math.geom.Vector3i;
import org.terasology.world.BlockEntityRegistry;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class RelativeInventoryBindingCustomObject implements CustomObject, InventoryBinding {
    private BlockEntityRegistry blockEntityRegistry;
    private Direction inventoryDirection;
    private boolean input;

    public RelativeInventoryBindingCustomObject(BlockEntityRegistry blockEntityRegistry,
                                                Direction inventoryDirection, boolean input) {
        this.blockEntityRegistry = blockEntityRegistry;
        this.inventoryDirection = inventoryDirection;
        this.input = input;
    }

    @Override
    public String getType() {
        return "INVENTORY_BINDING";
    }

    @Override
    public boolean isInput() {
        return input;
    }

    @Override
    public InventoryWithSlots getInventoryEntity(int line, ComputerCallback computerCallback) throws ExecutionException {
        Vector3i computerLocation = computerCallback.getComputerLocation();
        Vector3i directionVector = inventoryDirection.getVector3i();
        Vector3i inventoryLocation = new Vector3i(
                computerLocation.x + directionVector.x,
                computerLocation.y + directionVector.y,
                computerLocation.z + directionVector.z);

        EntityRef blockEntityAt = blockEntityRegistry.getBlockEntityAt(inventoryLocation);
        if (!blockEntityAt.hasComponent(InventoryComponent.class)
                || !blockEntityAt.hasComponent(InventoryAccessComponent.class))
            throw new ExecutionException(line, "Unable to locate accessible inventory with this binding");

        List<Integer> slots = getAccessibleSlots(blockEntityAt);

        return new InventoryWithSlots(blockEntityAt, Collections.unmodifiableList(slots));
    }

    private List<Integer> getAccessibleSlots(EntityRef blockEntityAt) {
        InventoryAccessComponent access = blockEntityAt.getComponent(InventoryAccessComponent.class);

        List<Integer> slots;
        Map<String, IntegerRange> slotMap = getCorrectSlotMap(access);
        IntegerRange integerRange = slotMap.get(inventoryDirection.reverse().toSide().name().toLowerCase());
        if (integerRange == null) {
            slots = Collections.emptyList();
        } else {
            slots = new LinkedList<>();

            Iterator<Integer> iterator = integerRange.createIterator();
            while (iterator.hasNext()) {
                slots.add(iterator.next());
            }
        }
        return slots;
    }

    private Map<String, IntegerRange> getCorrectSlotMap(InventoryAccessComponent access) {
        Map<String, IntegerRange> slotMap;
        if (input) {
            slotMap = access.input;
        } else {
            slotMap = access.output;
        }
        return slotMap;
    }
}
