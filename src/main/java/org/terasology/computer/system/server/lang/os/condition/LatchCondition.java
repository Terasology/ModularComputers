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

public class LatchCondition extends AbstractConditionCustomObject {
    public boolean released;

    public Object result;
    public ExecutionException error;

    public void release(Object result) {
        this.result = result;
        released = true;
    }

    public void releaseWithError(ExecutionException executionException) {
        this.error = executionException;
        released = true;
    }

    @Override
    public int getCreationDelay() {
        return 0;
    }

    @Override
    public ResultAwaitingCondition createAwaitingCondition() {
        return new ResultAwaitingCondition() {
            @Override
            public Variable getReturnValue() {
                return new Variable(result);
            }

            @Override
            public boolean isMet() throws ExecutionException {
                if (released && error != null)
                    throw error;
                return released;
            }
        };
    }
}
