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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Variable variable = (Variable) o;

        if (_type != variable._type) return false;
        if (_value != null ? !_value.equals(variable._value) : variable._value != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = _value != null ? _value.hashCode() : 0;
        result = 31 * result + (_type != null ? _type.hashCode() : 0);
        return result;
    }
}
