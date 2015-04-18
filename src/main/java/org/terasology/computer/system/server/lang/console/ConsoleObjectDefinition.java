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
package org.terasology.computer.system.server.lang.console;

import com.gempukku.lang.ExecutionContext;
import com.gempukku.lang.ObjectDefinition;
import com.gempukku.lang.Variable;

public class ConsoleObjectDefinition implements ObjectDefinition {
	private Variable _append = new Variable(new AppendToConsoleFunction());
	private Variable _clear = new Variable(new ClearConsoleFunction());
	private Variable _write = new Variable(new WriteToConsoleFunction());

	@Override
	public Variable getMember(ExecutionContext context, String name) {
		if (name.equals("append"))
			return _append;
		else if (name.equals("clear"))
			return _clear;
		else if (name.equals("write"))
			return _write;
		return new Variable(null);
	}
}
