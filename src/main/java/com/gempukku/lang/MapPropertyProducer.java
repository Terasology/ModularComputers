// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package com.gempukku.lang;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class MapPropertyProducer implements PropertyProducer {
    @Override
    public Variable exposePropertyFor(ExecutionContext context, Variable object, String property) throws ExecutionException {
        Map<String, Variable> map = (Map<String, Variable>) object.getValue();
        if (property.equals("size")) {
            return new Variable(new MapSizeFunction(map));
        }
        return new Variable(null);
    }

    private static final class MapSizeFunction extends AbstractFunctionExecutable {
        private Map<String, Variable> map;

        private MapSizeFunction(Map<String, Variable> map) {
            this.map = map;
        }

        @Override
        protected Object executeFunction(int line, Map<String, Variable> parameters) throws ExecutionException {
            return map.size();
        }

        @Override
        protected int getDuration() {
            return 100;
        }

        @Override
        public Collection<String> getParameterNames() {
            return Collections.emptySet();
        }
    }
}
