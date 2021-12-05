// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.computer.module.inventory;

import com.gempukku.lang.CustomObject;
import com.gempukku.lang.ExecutionException;
import org.joml.RoundingMode;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.joml.Vector3ic;
import org.terasology.computer.context.ComputerCallback;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.module.inventory.components.InventoryAccessComponent;
import org.terasology.module.inventory.components.InventoryComponent;
import org.terasology.engine.math.Direction;
import org.terasology.engine.math.IntegerRange;
import org.terasology.engine.world.BlockEntityRegistry;

import java.util.Collection;
import java.util.Collections;
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
    public Collection<String> getType() {
        return Collections.singleton("INVENTORY_BINDING");
    }

    @Override
    public boolean isInput() {
        return input;
    }

    @Override
    public int sizeOf() {
        return 4;
    }

    @Override
    public InventoryWithSlots getInventoryEntity(int line, ComputerCallback computerCallback) throws ExecutionException {
        Vector3f computerLocation = computerCallback.getComputerLocation();
        Vector3ic directionVector = inventoryDirection.asVector3i();
        Vector3i inventoryLocation = new Vector3i(new Vector3f(
                computerLocation.x + directionVector.x(),
                computerLocation.y + directionVector.y(),
                computerLocation.z + directionVector.z()), RoundingMode.FLOOR);

        EntityRef blockEntityAt = blockEntityRegistry.getBlockEntityAt(inventoryLocation);
        if (!blockEntityAt.hasComponent(InventoryComponent.class)
                || !blockEntityAt.hasComponent(InventoryAccessComponent.class)) {
            throw new ExecutionException(line, "Unable to locate accessible inventory with this binding");
        }

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

            for (int slot : integerRange) {
                slots.add(slot);
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
