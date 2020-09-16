// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.modularcomputers.shadedlibs.com.gempukku.lang;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ListPropertyProducer implements PropertyProducer {
    @Override
    public Variable exposePropertyFor(ExecutionContext context, Variable object, String property) throws ExecutionException {
        List<Variable> list = (List<Variable>) object.getValue();
        if (property.equals("size"))
            return new Variable(new SizeFunction(list));
        else if (property.equals("add"))
            return new Variable(new AddElementFunction(list));
        else if (property.equals("remove"))
            return new Variable(new RemoveElementFunction(list));
        return new Variable(null);
    }

    private static class RemoveElementFunction extends AbstractFunctionExecutable {
        private final List<Variable> _list;

        private RemoveElementFunction(List<Variable> list) {
            _list = list;
        }

        @Override
        protected int getDuration() {
            return 100;
        }

        @Override
        public Collection<String> getParameterNames() {
            return Arrays.asList("index");
        }

        @Override
        protected Object executeFunction(int line, Map<String, Variable> parameters) throws ExecutionException {
            final Variable indexVar = parameters.get("index");
            if (indexVar.getType() != Variable.Type.NUMBER)
                throw new ExecutionException(line, "Expected NUMBER index in remove()");

            int index = ((Number) indexVar.getValue()).intValue();
            if (index < 0 || index >= _list.size())
                throw new ExecutionException(line, "Index out of bounds in remove()");

            return _list.remove(index).getValue();
        }
    }

    private static class AddElementFunction extends AbstractFunctionExecutable {
        private final List<Variable> _list;

        private AddElementFunction(List<Variable> list) {
            _list = list;
        }

        @Override
        protected int getDuration() {
            return 100;
        }

        @Override
        public Collection<String> getParameterNames() {
            return Arrays.asList("element");
        }

        @Override
        protected Object executeFunction(int line, Map<String, Variable> parameters) throws ExecutionException {
            final Object value = parameters.get("element").getValue();
            _list.add(new Variable(value));
            return null;
        }
    }

    private static class SizeFunction extends AbstractFunctionExecutable {
        private final List<Variable> _list;

        private SizeFunction(List<Variable> list) {
            _list = list;
        }

        @Override
        protected int getDuration() {
            return 10;
        }

        @Override
        public Collection<String> getParameterNames() {
            return Collections.emptySet();
        }

        @Override
        protected Object executeFunction(int line, Map<String, Variable> parameters) throws ExecutionException {
            return _list.size();
        }
    }
}
