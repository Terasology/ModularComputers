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
package org.terasology.computer.system.server.lang.os.condition;

import com.gempukku.lang.ExecutionException;
import com.gempukku.lang.Variable;

public abstract class InventoryCondition extends LatchCondition<Object> {
    private boolean released;

    private Object result;
    private ExecutionException error;

    /**
     * Check the state of this latch, called by the outside code to notify it, that the state of this condition might
     * have changed. If it returns true, it will be removed from the observed objects. Internally it should call release
     * or releaseWithError.
     * @return
     */
    public abstract boolean checkRelease();

    /**
     * Called by the outside code to notify it, that the state has permanently been affected in a way that will prevent
     * this condition to ever be true.
     */
    public abstract void cancelCondition();
}
