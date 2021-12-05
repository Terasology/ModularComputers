// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package com.gempukku.lang;

public interface ExecutionCostConfiguration {
    int getGetContextValue();

    int getSetContextValue();

    int getGetReturnValue();

    int getSetReturnValue();

    int getBreakBlock();

    int getDefineVariable();

    int getSetVariable();

    int getStackExecution();

    int getStackGroupExecution();

    int getSumValues();

    int getOtherMathOperation();

    int getCompareValues();

    int getResolveMember();
}
