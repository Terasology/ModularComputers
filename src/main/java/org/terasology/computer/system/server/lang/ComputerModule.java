// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.computer.system.server.lang;

import java.util.Collection;
import java.util.Map;

public interface ComputerModule {
    /**
     * Returns a string representing the module type. It is recommended to user Camel-Case string, with no spaces,
     * starting with a capital letter, i.e. "SampleModuleDiggingHoles".
     *
     * @return Module type.
     */
    String getModuleType();

    /**
     * Returns a module name, as displayed on tooltips.
     *
     * @return Module name for displaying on tooltips.
     */
    String getModuleName();

    /**
     * Checks if this module can be placed in an existing computer. This method is for modules that should not be placed
     * in multiples, or in combinations with other modules, as it allows modules to control the configuration of a
     * computer.
     *
     * @param computerModulesInstalled Already installed modules in the computer.
     * @return True, if it's ok to place this module in the computer passed as a parameter, false otherwise.
     */
    boolean canBePlacedInComputer(Collection<ComputerModule> computerModulesInstalled);

    /**
     * Checks if this module, that is already placed in the computer, is ok with adding a new module to the computer.
     * This method is for modules that should not be placed in multiples, or in combinations with other modules, as it
     * allows modules to control the configuration of a computer.
     *
     * @param computerModule New computer module that is being placed into the computer.
     * @return True, if it's ok to place the module in the computer passed as a parameter, false otherwise.
     */
    boolean acceptsNewModule(ComputerModule computerModule);

    /**
     * Returns a function with the specified name. To make the implementation easier, you can subclass
     * JavaFunctionExecutable class, that defines an easy to use interface for Java developers.
     *
     * @param name Name of the function this module supports.
     * @return Function that will be executed, when invoked by the program, or null if there is no function with this
     *         name.
     */
    ModuleMethodExecutable getMethodByName(String name);

    Map<String, ModuleMethodExecutable<?>> getAllMethods();
}
