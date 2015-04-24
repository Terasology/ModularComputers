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
package org.terasology.movingBlock;

import org.terasology.entitySystem.Component;
import org.terasology.math.geom.Vector3i;
import org.terasology.network.Replicate;
import org.terasology.world.block.Block;
import org.terasology.world.block.ForceBlockActive;

@Replicate
@ForceBlockActive
public class MovingBlockComponent implements Component {
    private Block blockToRender;
    private Vector3i locationFrom;
    private Vector3i locationTo;
    private long timeStart;
    private long timeEnd;

    public MovingBlockComponent() {
    }

    public MovingBlockComponent(Block blockToRender, Vector3i locationFrom, Vector3i locationTo, long timeStart, long timeEnd) {
        this.blockToRender = blockToRender;
        this.locationFrom = locationFrom;
        this.locationTo = locationTo;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
    }

    public Block getBlockToRender() {
        return blockToRender;
    }

    public Vector3i getLocationFrom() {
        return locationFrom;
    }

    public Vector3i getLocationTo() {
        return locationTo;
    }

    public long getTimeStart() {
        return timeStart;
    }

    public long getTimeEnd() {
        return timeEnd;
    }
}
