// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.modularcomputers.system.common;

import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.FunctionExecutable;
import org.terasology.modularcomputers.ui.documentation.MethodDocumentation;

public interface DocumentedFunctionExecutable extends FunctionExecutable {
    MethodDocumentation getMethodDocumentation();
}
