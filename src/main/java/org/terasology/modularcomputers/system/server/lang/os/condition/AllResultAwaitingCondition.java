// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.modularcomputers.system.server.lang.os.condition;

import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.ExecutionException;
import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.Variable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class AllResultAwaitingCondition implements ResultAwaitingCondition {
    private final List<ResultAwaitingCondition> awaitingConditions;
    private final Set<ResultAwaitingCondition> notMetConditions;

    public AllResultAwaitingCondition(List<ResultAwaitingCondition> awaitingConditions) {
        this.awaitingConditions = awaitingConditions;
        notMetConditions = new LinkedHashSet<>(this.awaitingConditions);
    }

    @Override
    public boolean isMet() throws ExecutionException {
        final Iterator<ResultAwaitingCondition> notMetIterator = notMetConditions.iterator();
        while (notMetIterator.hasNext()) {
            final ResultAwaitingCondition notMetCondition = notMetIterator.next();
            if (notMetCondition.isMet()) {
                notMetIterator.remove();
            }

        }
        return notMetConditions.isEmpty();
    }

    @Override
    public Variable getReturnValue() {
        List<Variable> result = new ArrayList<Variable>();
        for (ResultAwaitingCondition awaitingCondition : awaitingConditions) {
            result.add(awaitingCondition.getReturnValue());
        }

        return new Variable(result);
    }

    @Override
    public void dispose() {
        for (ResultAwaitingCondition awaitingCondition : awaitingConditions) {
            awaitingCondition.dispose();
        }
    }
}
