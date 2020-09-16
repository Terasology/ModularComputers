// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.modularcomputers.module.wireless;

import org.terasology.math.geom.Vector3i;

public abstract class SecureMessageAwaitingLatchCondition extends MessageAwaitingLatchCondition {
    private final String password;

    protected SecureMessageAwaitingLatchCondition(Vector3i locationTo, float rangeTo, String password) {
        super(locationTo, rangeTo);
        this.password = password;
    }

    public String getPassword() {
        return password;
    }
}
