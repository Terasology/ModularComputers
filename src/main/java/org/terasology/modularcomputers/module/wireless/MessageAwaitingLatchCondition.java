// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.modularcomputers.module.wireless;

import org.terasology.math.geom.Vector3i;
import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.Variable;
import org.terasology.modularcomputers.system.server.lang.os.condition.LatchCondition;

import java.util.Map;

public abstract class MessageAwaitingLatchCondition extends LatchCondition<Map<String, Variable>> {
    private final Vector3i locationTo;
    private final float rangeTo;

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
