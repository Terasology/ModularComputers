// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.modularcomputers.system.server.lang.os.condition;

import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.ExecutionException;

public abstract class InventoryCondition extends LatchCondition<Object> {
    private boolean released;

    private Object result;
    private ExecutionException error;

    /**
     * Check the state of this latch, called by the outside code to notify it, that the state of this condition might
     * have changed. If it returns true, it will be removed from the observed objects. Internally it should call release
     * or releaseWithError.
     *
     * @return
     */
    public abstract boolean checkRelease();

    /**
     * Called by the outside code to notify it, that the state has permanently been affected in a way that will prevent
     * this condition to ever be true.
     */
    public abstract void cancelCondition();
}
