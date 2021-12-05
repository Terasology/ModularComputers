// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package com.gempukku.lang;

public class ObjectPropertyProducer implements PropertyProducer {
    @Override
    public Variable exposePropertyFor(ExecutionContext context, Variable object, String property) throws ExecutionException {
        final ObjectDefinition objectDefinition = (ObjectDefinition) object.getValue();
        return objectDefinition.getMember(context, property);
    }
}
