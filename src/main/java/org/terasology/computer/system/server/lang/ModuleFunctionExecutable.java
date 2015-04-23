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
package org.terasology.computer.system.server.lang;

import com.gempukku.lang.ExecutionException;
import com.gempukku.lang.Variable;
import org.terasology.computer.context.ComputerCallback;

import java.util.Map;

public interface ModuleFunctionExecutable {
    public int getCpuCycleDuration();

    public int getMinimumExecutionTicks();

    public String[] getParameterNames();

    public default void onFunctionStart(int line, ComputerCallback computer, Map<String, Variable> parameters) throws ExecutionException {

    }

    public Object executeFunction(int line, ComputerCallback computer, Map<String, Variable> parameters) throws ExecutionException;
}
