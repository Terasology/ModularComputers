package com.gempukku.lang;

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
        private Map<String, Variable> _map;

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
        public String[] getParameterNames() {
            return new String[0];
        }
    }
}
