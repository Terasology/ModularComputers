// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package com.gempukku.lang;

public interface Execution {
    boolean hasNextExecution(ExecutionContext executionContext);

    ExecutionProgress executeNextStatement(ExecutionContext executionContext,
                                           ExecutionCostConfiguration configuration) throws ExecutionException;
}
