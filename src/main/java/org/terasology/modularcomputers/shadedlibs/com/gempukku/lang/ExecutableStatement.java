// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.modularcomputers.shadedlibs.com.gempukku.lang;

public interface ExecutableStatement {
    Execution createExecution();

    boolean requiresSemicolon();
}
