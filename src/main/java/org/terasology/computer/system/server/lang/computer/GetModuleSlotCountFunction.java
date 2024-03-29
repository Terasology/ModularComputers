// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.computer.system.server.lang.computer;

import com.gempukku.lang.ExecutionException;
import com.gempukku.lang.Variable;
import org.terasology.computer.context.ComputerCallback;
import org.terasology.computer.system.server.lang.TerasologyFunctionExecutable;

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
