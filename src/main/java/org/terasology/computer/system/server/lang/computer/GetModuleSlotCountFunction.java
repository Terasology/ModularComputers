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
