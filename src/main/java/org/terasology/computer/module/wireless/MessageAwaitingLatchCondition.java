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
package org.terasology.computer.module.wireless;

import com.gempukku.lang.Variable;
import org.terasology.computer.system.server.lang.os.condition.LatchCondition;
import org.terasology.math.geom.Vector3i;

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
