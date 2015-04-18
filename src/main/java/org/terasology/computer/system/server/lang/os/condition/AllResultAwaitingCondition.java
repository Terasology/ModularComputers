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
package org.terasology.computer.system.server.lang.os.condition;

import com.gempukku.lang.ExecutionException;
import com.gempukku.lang.Variable;

import java.util.*;

public class AllResultAwaitingCondition implements ResultAwaitingCondition {
	private List<ResultAwaitingCondition> _awaitingConditions;
	private Set<ResultAwaitingCondition> _notMetConditions;

	public AllResultAwaitingCondition(List<ResultAwaitingCondition> awaitingConditions) {
		_awaitingConditions = awaitingConditions;
		_notMetConditions = new LinkedHashSet<ResultAwaitingCondition>(_awaitingConditions);
	}

	@Override
	public boolean isMet() throws ExecutionException {
		final Iterator<ResultAwaitingCondition> notMetIterator = _notMetConditions.iterator();
		while (notMetIterator.hasNext()) {
			final ResultAwaitingCondition notMetCondition = notMetIterator.next();
			if (notMetCondition.isMet())
				notMetIterator.remove();

		}
		return _notMetConditions.isEmpty();
	}

	@Override
	public Variable getReturnValue() {
		List<Variable> result = new ArrayList<Variable>();
		for (ResultAwaitingCondition awaitingCondition : _awaitingConditions)
			result.add(awaitingCondition.getReturnValue());

		return new Variable(result);
	}
}
