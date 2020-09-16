// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.modularcomputers.system.server.lang.computer;

import org.terasology.modularcomputers.context.ComputerCallback;
import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.ExecutionException;
import org.terasology.modularcomputers.shadedlibs.com.gempukku.lang.Variable;
import org.terasology.modularcomputers.system.server.lang.TerasologyFunctionExecutable;

import java.util.Map;

public class GetModuleSlotCountFunction extends TerasologyFunctionExecutable {
    public GetModuleSlotCountFunction() {
        super("Returns number of module slots in this computer.", "Number",
                "Number of module slots in this computer.");

        addExample("This example prints out the number of module slots this computer has.",
                "console.append(\"This computer has \" + computer.getModuleSlotCount() + \" module slots.\");"
        );
    }

    @Override
    protected int getDuration() {
        return 100;
    }

    @Override
    protected Object executeFunction(int line, ComputerCallback computer, Map<String, Variable> parameters) throws ExecutionException {
        return computer.getModuleSlotsCount();
    }
}
