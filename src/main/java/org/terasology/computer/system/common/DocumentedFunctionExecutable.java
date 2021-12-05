// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.computer.system.common;

import com.gempukku.lang.FunctionExecutable;
import org.terasology.computer.ui.documentation.MethodDocumentation;

public interface DocumentedFunctionExecutable extends FunctionExecutable {
    MethodDocumentation getMethodDocumentation();
}
