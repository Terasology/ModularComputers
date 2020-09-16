// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.modularcomputers.system.server.lang.os.condition;

import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.ExecutionException;
import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.Variable;

import java.util.ArrayList;
import java.util.List;

public class AnyResultAwaitingCondition implements ResultAwaitingCondition {
    private final List<ResultAwaitingCondition> awaitingConditions;
    private int metConditionIndex = -1;
    private boolean disposed;

    public AnyResultAwaitingCondition(List<ResultAwaitingCondition> awaitingConditions) {
        this.awaitingConditions = awaitingConditions;
    }

    @Override
    public boolean isMet() throws ExecutionException {
        if (metConditionIndex != -1) {
            return true;
        }

        final int size = awaitingConditions.size();
        for (int i = 0; i < size; i++) {
            final ResultAwaitingCondition awaitingCondition = awaitingConditions.get(i);
            if (awaitingCondition.isMet()) {
                metConditionIndex = i;
                dispose();
                return true;
            }
        }

        return false;
    }

    @Override
    public Variable getReturnValue() {
        List<Variable> result = new ArrayList<Variable>();
        result.add(new Variable(metConditionIndex));
        result.add(awaitingConditions.get(metConditionIndex).getReturnValue());
        return new Variable(result);
    }

    @Override
    public void dispose() {
        if (!disposed) {
            disposed = true;
            for (ResultAwaitingCondition awaitingCondition : awaitingConditions) {
                awaitingCondition.dispose();
            }
        }
    }
}
