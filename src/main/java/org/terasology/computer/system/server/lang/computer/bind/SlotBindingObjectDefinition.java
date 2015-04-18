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
package org.terasology.computer.system.server.lang.computer.bind;


import com.gempukku.lang.ExecutionContext;
import com.gempukku.lang.ObjectDefinition;
import com.gempukku.lang.Variable;
import org.terasology.computer.context.ComputerCallback;
import org.terasology.computer.context.TerasologyComputerExecutionContext;
import org.terasology.computer.system.server.lang.ComputerModule;
import org.terasology.computer.system.server.lang.ModuleFunctionExecutable;

public class SlotBindingObjectDefinition implements ObjectDefinition {
	private int _slotNo;

	public SlotBindingObjectDefinition(int slotNo) {
		_slotNo = slotNo;
	}

	@Override
	public Variable getMember(ExecutionContext context, String name) {
		final TerasologyComputerExecutionContext minecraftExecutionContext = (TerasologyComputerExecutionContext) context;
		final ComputerCallback computerCallback = minecraftExecutionContext.getComputerCallback();

		final ComputerModule module = computerCallback.getModule(_slotNo);
		if (module == null)
			return new Variable(null);

		final ModuleFunctionExecutable moduleFunction = module.getFunctionByName(name);

		if (moduleFunction != null) {
			return new Variable(new BindingFunctionWrapper(module, _slotNo, new ModuleFunctionAdapter(_slotNo, moduleFunction)));
		} else {
			return new Variable(null);
		}
	}
}
