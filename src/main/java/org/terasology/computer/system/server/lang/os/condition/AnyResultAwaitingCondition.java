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
import java.util.List;

public class AnyResultAwaitingCondition implements ResultAwaitingCondition {
    private List<ResultAwaitingCondition> awaitingConditions;
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
