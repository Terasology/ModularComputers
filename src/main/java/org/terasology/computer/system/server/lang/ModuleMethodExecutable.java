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

/**
 * Interface to define what a computer module method does.
 */
public interface ModuleMethodExecutable<T> {
    /**
     * Returns a number of the "virtual" CPU cycles consumed by execution of this method.
     * @return The number of CPU cycles consumed.
     */
    public int getCpuCycleDuration();

    /**
     * Returns a time consumed by execution of this method. In most cases it will be 0, as the CPU cycle duration is
     * enough to steer it, however if a specific action should have a minimum duration, even on fastest computers, this
     * method is used. A good example would be a computer movement.
     * @return Time in ms of world time that this operation should take at the minimum (CPU cycles bound as well).
     * @param line
     * @param computer
     * @param parameters
     */
    public default int getMinimumExecutionTime(int line, ComputerCallback computer, Map<String, Variable> parameters) throws ExecutionException { return 0; }

    /**
     * Names of the parameters this method accepts. This parameter names are then passed to the onFunctionStart() and
     * executeFunction() methods of this interface.
     * @return
     */
    public String[] getParameterNames();

    /**
     * Method called at the beginning of the execution.
     * @param line Line of code where the method code is made - useful for signalizing errors for debug purposes.
     * @param computer ComputerCallback allowing to query information about the computer.
     * @param parameters Parameters passed to the method.
     * @return The object that will be passed to the onFunctionEnd() method as a parameter;
     * @throws ExecutionException Used to signal a problem in the code or some other problem that should end the program.
     */
    public default T onFunctionStart(int line, ComputerCallback computer, Map<String, Variable> parameters) throws ExecutionException { return null; }

    /**
     * Interface method called at the end of CPU cycles and/or the time allotted to this method.
     * @param line Line of code where the method code is made - useful for signalizing errors for debug purposes.
     * @param computer ComputerCallback allowing to query information about the computer.
     * @param parameters Parameters passed to the method.
     * @param onFunctionStartResult
     * @return An object that should be placed in the variable that is a result of calling this method. Please note
     * only objects of types defined in Variable class should be returned. If you wish to return a Map, it has to be
     * a Map&lt;String, Variable&gt;. For a List - you have to return List&lt;Variable&gt;.
     * @throws ExecutionException Used to signal a problem in the code or some other problem that should end the program.
     */
    public Object onFunctionEnd(int line, ComputerCallback computer, Map<String, Variable> parameters, T onFunctionStartResult) throws ExecutionException;
}
