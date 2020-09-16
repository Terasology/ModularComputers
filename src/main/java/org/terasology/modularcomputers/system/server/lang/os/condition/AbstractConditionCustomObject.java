// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.modularcomputers.system.server.lang.os.condition;

import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.CustomObject;
import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.ExecutionException;

import java.util.Collection;
import java.util.Collections;

public abstract class AbstractConditionCustomObject implements CustomObject {
    @Override
    public Collection<String> getType() {
        return Collections.singleton("CONDITION");
    }

    public abstract ResultAwaitingCondition createAwaitingCondition() throws ExecutionException;
}
