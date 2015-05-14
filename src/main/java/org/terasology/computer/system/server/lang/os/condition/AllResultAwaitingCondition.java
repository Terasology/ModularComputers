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
