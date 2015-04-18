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


import com.gempukku.lang.ExecutionContext;
import com.gempukku.lang.ObjectDefinition;
import com.gempukku.lang.Variable;

public class ComputerObjectDefinition implements ObjectDefinition {
	private Variable _bindModule = new Variable(new BindModuleFunction());
	private Variable _bindModuleOfType = new Variable(new BindFirstModuleOfTypeFunction());
	private Variable _getModuleSlotCount = new Variable(new GetModuleSlotCountFunction());
	private Variable _getModuleType = new Variable(new GetModuleTypeFunction());

	@Override
	public Variable getMember(ExecutionContext context, String name) {
		if (name.equals("bindModule"))
			return _bindModule;
		else if (name.equals("bindModuleOfType"))
			return _bindModuleOfType;
		else if (name.equals("getModuleSlotCount"))
			return _getModuleSlotCount;
		else if (name.equals("getModuleType"))
			return _getModuleType;
		return new Variable(null);
	}
}
