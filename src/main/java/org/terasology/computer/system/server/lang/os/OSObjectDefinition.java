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
package org.terasology.computer.system.server.lang.os;

import com.gempukku.lang.ExecutionContext;
import com.gempukku.lang.ObjectDefinition;
import com.gempukku.lang.Variable;

public class OSObjectDefinition implements ObjectDefinition {
	private Variable _parseFloat = new Variable(new ParseFloatFunction());
	private Variable _parseInt = new Variable(new ParseIntFunction());
	private Variable _format = new Variable(new FormatFunction());

	private Variable _typeOf = new Variable(new TypeOfFunction());

	private Variable _waitFor = new Variable(new WaitForFunction());

	private Variable _createSleepMs = new Variable(new CreateSleepMsFunction());
	private Variable _createSleepTick = new Variable(new CreateSleepTickFunction());
	private Variable _any = new Variable(new AnyFunction());
	private Variable _all = new Variable(new AllFunction());

	@Override
	public Variable getMember(ExecutionContext context, String name) {
		if (name.equals("parseFloat"))
			return _parseFloat;
		else if (name.equals("parseInt"))
			return _parseInt;
		else if (name.equals("typeOf"))
			return _typeOf;
		else if (name.equals("waitFor"))
			return _waitFor;
		else if (name.equals("createSleepMs"))
			return _createSleepMs;
		else if (name.equals("createSleepTick"))
			return _createSleepTick;
		else if (name.equals("any"))
			return _any;
		else if (name.equals("all"))
			return _all;
		else if (name.equals("format"))
			return _format;

		return new Variable(null);
	}
}
