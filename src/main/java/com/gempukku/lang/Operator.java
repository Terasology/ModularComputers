// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package com.gempukku.lang;

public class Operator {
    public static final Operator MEMBER_ACCESS = new Operator(1, 1, true, true, false, true);
    public static final Operator MAPPED_ACCESS = Operator.createParameterUnary(1, 1, true, true, "]");
    public static final Operator FUNCTION_CALL = Operator.createParameterUnary(1, 1, true, false, ")");

    public static final Operator NEGATIVE = Operator.createUnary(1, 2, false, true);
    public static final Operator NOT = Operator.createUnary(1, 2, false, true);
    public static final Operator PRE_INCREMENT = Operator.createUnary(2, 2, false, true);
    public static final Operator POST_INCREMENT = Operator.createUnary(2, 2, false, false);
    public static final Operator PRE_DECREMENT = Operator.createUnary(2, 2, false, true);
    public static final Operator POST_DECREMENT = Operator.createUnary(2, 2, false, false);

    public static final Operator MULTIPLY = Operator.createBinaryLeftAssociative(1, 3);
    public static final Operator DIVIDE = Operator.createBinaryLeftAssociative(1, 3);
    public static final Operator MOD = Operator.createBinaryLeftAssociative(1, 3);

    public static final Operator ADD = Operator.createBinaryLeftAssociative(1, 4);
    public static final Operator SUBTRACT = Operator.createBinaryLeftAssociative(1, 4);

    public static final Operator GREATER_OR_EQUAL = Operator.createBinaryLeftAssociative(2, 6);
    public static final Operator GREATER = Operator.createBinaryLeftAssociative(1, 6);
    public static final Operator LESS_OR_EQUAL = Operator.createBinaryLeftAssociative(2, 6);
    public static final Operator LESS = Operator.createBinaryLeftAssociative(1, 6);

    public static final Operator EQUALS = Operator.createBinaryLeftAssociative(2, 7);
    public static final Operator NOT_EQUALS = Operator.createBinaryLeftAssociative(2, 7);

    public static final Operator AND = Operator.createBinaryLeftAssociative(2, 11);

    public static final Operator OR = Operator.createBinaryLeftAssociative(2, 12);

    public static final Operator ASSIGNMENT = Operator.createBinaryRightAssociative(1, 14);
    public static final Operator ADD_ASSIGN = Operator.createBinaryRightAssociative(2, 14);
    public static final Operator SUBTRACT_ASSIGN = Operator.createBinaryRightAssociative(2, 14);
    public static final Operator MULTIPLY_ASSIGN = Operator.createBinaryRightAssociative(2, 14);
    public static final Operator DIVIDE_ASSIGN = Operator.createBinaryRightAssociative(2, 14);
    public static final Operator MOD_ASSIGN = Operator.createBinaryRightAssociative(2, 14);

    private int _priority;
    private int _consumeLength;
    private boolean _leftAssociative;
    private boolean _binary;
    private boolean _hasParameters;
    private boolean _exactlyOneParameter;
    private String _parametersClosing;
    private boolean _namedOnRight;
    private boolean _pre;

    public Operator() {
    }

    private Operator(int consumeLength, int priority, boolean leftAssociative, boolean binary, boolean hasParameters,
                     boolean namedOnRight) {
        _consumeLength = consumeLength;
        _priority = priority;
        _leftAssociative = leftAssociative;
        _binary = binary;
        _hasParameters = hasParameters;
        _namedOnRight = namedOnRight;
    }

    private static Operator createUnary(int consumeLength, int priority, boolean leftAssociative, boolean pre) {
        Operator operator = new Operator();
        operator._consumeLength = consumeLength;
        operator._priority = priority;
        operator._leftAssociative = leftAssociative;
        operator._pre = pre;
        return operator;
    }

    private static Operator createParameterUnary(int consumeLength, int priority, boolean leftAssociative,
                                                 boolean exactlyOneParameter, String parametersClosing) {
        Operator operator = new Operator();
        operator._consumeLength = consumeLength;
        operator._priority = priority;
        operator._leftAssociative = leftAssociative;
        operator._exactlyOneParameter = exactlyOneParameter;
        operator._hasParameters = true;
        operator._parametersClosing = parametersClosing;
        return operator;
    }

    private static Operator createBinaryLeftAssociative(int consumeLength, int priority) {
        Operator operator = new Operator();
        operator._consumeLength = consumeLength;
        operator._priority = priority;
        operator._leftAssociative = true;
        operator._binary = true;
        return operator;
    }

    private static Operator createBinaryRightAssociative(int consumeLength, int priority) {
        Operator operator = new Operator();
        operator._consumeLength = consumeLength;
        operator._priority = priority;
        operator._leftAssociative = false;
        operator._binary = true;
        return operator;
    }

    public boolean isPre() {
        return _pre;
    }

    public boolean isNamedOnRight() {
        return _namedOnRight;
    }

    public int getConsumeLength() {
        return _consumeLength;
    }

    public boolean isLeftAssociative() {
        return _leftAssociative;
    }

    public int getPriority() {
        return _priority;
    }

    public boolean isBinary() {
        return _binary;
    }

    public boolean isHasParameters() {
        return _hasParameters;
    }

    public boolean exactlyOneParameter() {
        return _exactlyOneParameter;
    }

    public String getParametersClosing() {
        return _parametersClosing;
    }
}
