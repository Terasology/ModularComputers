// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package com.gempukku.lang;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Variable {
    public enum Type {
        NULL, STRING, NUMBER, BOOLEAN, FUNCTION, LIST, MAP, OBJECT, CUSTOM_OBJECT
    }

    private Object value;
    private Type type;

    public Variable(Object value) {
        setValue(value);
    }

    public void setValue(Object value) {
        this.value = value;
        if (value == null) {
            type = Type.NULL;
        } else if (value instanceof String) {
            type = Type.STRING;
        } else if (value instanceof Number) {
            type = Type.NUMBER;
        } else if (value instanceof Map) {
            type = Type.MAP;
        } else if (value instanceof Boolean) {
            type = Type.BOOLEAN;
        } else if (value instanceof FunctionExecutable) {
            type = Type.FUNCTION;
        } else if (value instanceof ObjectDefinition) {
            type = Type.OBJECT;
        } else if (value instanceof List) {
            type = Type.LIST;
        } else if (value instanceof CustomObject) {
            type = Type.CUSTOM_OBJECT;
        } else {
            throw new UnsupportedOperationException("Unknown type of variable value: " + value.getClass().getSimpleName());
        }
    }

    public Type getType() {
        return type;
    }

    public Object getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Variable variable = (Variable) o;

        if (type != variable.type) {
            return false;
        }
        return Objects.equals(value, variable.value);
    }

    @Override
    public int hashCode() {
        int result = value != null ? value.hashCode() : 0;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        return result;
    }
}
