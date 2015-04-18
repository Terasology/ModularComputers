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

import com.gempukku.lang.ExecutionException;
import com.gempukku.lang.Variable;
import org.terasology.computer.context.ComputerCallback;
import org.terasology.computer.system.server.lang.TerasologyFunctionExecutable;

import java.util.Map;

public class WriteToConsoleFunction extends TerasologyFunctionExecutable {
	@Override
	protected int getDuration() {
		return 100;
	}

	@Override
	public String[] getParameterNames() {
		return new String[]{"x", "y", "text"};
	}

	@Override
	protected Object executeFunction(int line, ComputerCallback computer, Map<String, Variable> parameters) throws ExecutionException {
		final Variable x = parameters.get("x");
		final Variable y = parameters.get("y");
		final Variable text = parameters.get("text");
		if (x.getType() != Variable.Type.NUMBER)
			throw new ExecutionException(line, "Expected NUMBER in write()");
		if (y.getType() != Variable.Type.NUMBER)
			throw new ExecutionException(line, "Expected NUMBER in write()");
		if (text.getType() != Variable.Type.STRING)
			throw new ExecutionException(line, "Expected STRING in write()");

		computer.getConsole().setCharacters(((Number) x.getValue()).intValue(), ((Number) y.getValue()).intValue(), (String) text.getValue());

		return null;
	}
}
