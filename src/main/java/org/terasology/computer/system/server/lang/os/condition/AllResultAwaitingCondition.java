// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.computer.system.server.lang.os.condition;

import com.gempukku.lang.ExecutionException;
import com.gempukku.lang.Variable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class AllResultAwaitingCondition implements ResultAwaitingCondition {
    private List<ResultAwaitingCondition> awaitingConditions;
    private Set<ResultAwaitingCondition> notMetConditions;

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
