// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.computer.system.server.lang.os.condition;

import com.gempukku.lang.CustomObject;
import com.gempukku.lang.ExecutionException;

import java.util.Collection;
import java.util.Collections;

public abstract class AbstractConditionCustomObject implements CustomObject {
    @Override
    public Collection<String> getType() {
        return Collections.singleton("CONDITION");
    }

    public abstract ResultAwaitingCondition createAwaitingCondition() throws ExecutionException;
}
