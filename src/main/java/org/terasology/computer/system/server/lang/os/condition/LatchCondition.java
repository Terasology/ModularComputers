// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.computer.system.server.lang.os.condition;

import com.gempukku.lang.ExecutionException;
import com.gempukku.lang.Variable;

public abstract class LatchCondition<T> extends AbstractConditionCustomObject {
    private boolean released;

    private T result;
    private ExecutionException error;

    public void release(T result) {
        this.result = result;
        released = true;
    }

    public void releaseWithError(ExecutionException executionException) {
        this.error = executionException;
        released = true;
    }

    @Override
    public int sizeOf() {
        return 4;
    }

    @Override
    public ResultAwaitingCondition createAwaitingCondition() throws ExecutionException {
        Runnable disposeRunnable = registerAwaitingCondition();
        return new ResultAwaitingCondition() {
            @Override
            public Variable getReturnValue() {
                return new Variable(result);
            }

            @Override
            public boolean isMet() throws ExecutionException {
                if (released && error != null) {
                    throw error;
                }
                return released;
            }

            @Override
            public void dispose() {
                if (disposeRunnable != null) {
                    disposeRunnable.run();
                }
            }
        };
    }

    /**
     * Called when code starts waiting on this condition. The Runnable returned is invoked when the condition is no
     * longer being waited on - should be used for cleaning up any objects setup to accommodate waiting for the
     * condition.
     *
     * @return Runnable that is executed when the condition is no longer being waited on. Could be <code>null</code> if
     *         no clean up is needed.
     */
    protected abstract Runnable registerAwaitingCondition() throws ExecutionException;
}
