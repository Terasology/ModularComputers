// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.modularcomputers.shadedlibs.com.gempukku.lang;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class MapPropertyProducer implements PropertyProducer {
    @Override
    public Variable exposePropertyFor(ExecutionContext context, Variable object, String property) throws ExecutionException {
        Map<String, Variable> map = (Map<String, Variable>) object.getValue();
        if (property.equals("size"))
            return new Variable(new MapSizeFunction(map));
        return new Variable(null);
    }

    private static class MapSizeFunction extends AbstractFunctionExecutable {
        private final Map<String, Variable> _map;

        private MapSizeFunction(Map<String, Variable> map) {
            _map = map;
        }

        @Override
        protected Object executeFunction(int line, Map<String, Variable> parameters) throws ExecutionException {
            return _map.size();
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
