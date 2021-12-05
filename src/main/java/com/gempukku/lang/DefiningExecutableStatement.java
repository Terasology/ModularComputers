// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package com.gempukku.lang;

public interface DefiningExecutableStatement extends ExecutableStatement {
    String getDefinedVariableName();
}
