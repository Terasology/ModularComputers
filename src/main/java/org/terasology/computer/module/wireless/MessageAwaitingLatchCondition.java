// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.computer.module.wireless;

import com.gempukku.lang.Variable;
import org.joml.Vector3i;
import org.terasology.computer.system.server.lang.os.condition.LatchCondition;

import java.util.Map;

public abstract class MessageAwaitingLatchCondition extends LatchCondition<Map<String, Variable>> {
    private Vector3i locationTo;
    private float rangeTo;

    public MessageAwaitingLatchCondition(Vector3i locationTo, float rangeTo) {
        this.locationTo = locationTo;
        this.rangeTo = rangeTo;
    }

    public Vector3i getLocationTo() {
        return locationTo;
    }

    public float getRangeTo() {
        return rangeTo;
    }
}
