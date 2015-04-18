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

import org.terasology.computer.context.ComputerCallback;

public interface ComputerModule {
    /**
     * Returns a string representing the module type. It is recommended to user Camel-Case string, with no spaces,
     * starting with a capital letter, i.e. "SampleModuleDiggingHoles".
     *
     * @return Module type.
     */
    public String getModuleType();

    /**
     * Returns a module name, as displayed on tooltips.
     *
     * @return Module name for displaying on tooltips.
     */
    public String getModuleName();

    /**
     * Checks if this module can be placed in an existing computer.
     * This method is for modules that should not be placed in multiples, or in combinations with other modules, as
     * it allows modules to control the configuration of a computer.
     *
     * @param computerCallback
     * @return True, if it's ok to place this module in the computer passed as a parameter, false otherwise.
     */
    public boolean canBePlacedInComputer(ComputerCallback computerCallback);

    /**
     * Checks if this module, that is already placed in the computer, is ok with adding a new module to the computer.
     * This method is for modules that should not be placed in multiples, or in combinations with other modules, as
     * it allows modules to control the configuration of a computer.
     *
     * @param computerCallback
     * @param computerModule	 New computer module that is being placed into the computer.
     * @return True, if it's ok to place the module in the computer passed as a parameter, false otherwise.
     */
    public boolean acceptsNewModule(ComputerCallback computerCallback, ComputerModule computerModule);

    /**
     * Returns a function with the specified name. To make the implementation easier, you can subclass
     * JavaFunctionExecutable class, that defines an easy to use interface for Java developers.
     *
     * @param name Name of the function this module supports.
     * @return Function that will be executed, when invoked by the program, or null if there is no function with this
     *         name.
     */
    public ModuleFunctionExecutable getFunctionByName(String name);

}
