package com.gempukku.lang;

import java.util.List;
import java.util.Map;

public class Variable {
    public enum Type {NULL, STRING, NUMBER, BOOLEAN, FUNCTION, LIST, MAP, OBJECT, CUSTOM_OBJECT}

    private Object _value;
    private Type _type;

    public Variable(Object value) {
        setValue(value);
    }

    public void setValue(Object value) {
        _value = value;
        if (value == null) {
            _type = Type.NULL;
        } else if (value instanceof String) {
            _type = Type.STRING;
        } else if (value instanceof Number) {
            _type = Type.NUMBER;
        } else if (value instanceof Map) {
            _type = Type.MAP;
        } else if (value instanceof Boolean) {
            _type = Type.BOOLEAN;
        } else if (value instanceof FunctionExecutable) {
            _type = Type.FUNCTION;
        } else if (value instanceof ObjectDefinition) {
            _type = Type.OBJECT;
        } else if (value instanceof List) {
            _type = Type.LIST;
        } else if (value instanceof CustomObject) {
            _type = Type.CUSTOM_OBJECT;
        } else
            throw new UnsupportedOperationException("Unknown type of variable value: " + value.getClass().getSimpleName());
    }

    public Type getType() {
        return _type;
    }

    public Object getValue() {
        return _value;
    }
}
