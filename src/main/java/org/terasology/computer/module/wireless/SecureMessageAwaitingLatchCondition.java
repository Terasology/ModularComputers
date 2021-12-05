// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.computer.module.wireless;


import org.joml.Vector3i;

public abstract class SecureMessageAwaitingLatchCondition extends MessageAwaitingLatchCondition {
    private String password;

    protected SecureMessageAwaitingLatchCondition(Vector3i locationTo, float rangeTo, String password) {
        super(locationTo, rangeTo);
        this.password = password;
    }

    public String getPassword() {
        return password;
    }
}
