package com.gempukku.lang;

public class ObjectPropertyProducer implements PropertyProducer {
    @Override
    public Variable exposePropertyFor(ExecutionContext context, Variable object, String property) throws ExecutionException {
        final ObjectDefinition objectDefinition = (ObjectDefinition) object.getValue();
        return objectDefinition.getMember(context, property);
    }
}
