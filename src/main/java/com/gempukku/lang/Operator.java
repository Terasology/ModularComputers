// Copyright 2021 The Terasology Foundation
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

    private int priority;
    private int consumeLength;
    private boolean leftAssociative;
    private boolean binary;
    private boolean hasParameters;
    private boolean exactlyOneParameter;
    private String parametersClosing;
    private boolean namedOnRight;
    private boolean pre;

    public Operator() {
    }

    private Operator(int consumeLength, int priority, boolean leftAssociative, boolean binary,
                     boolean hasParameters, boolean namedOnRight) {
        this.consumeLength = consumeLength;
        this.priority = priority;
        this.leftAssociative = leftAssociative;
        this.binary = binary;
        this.hasParameters = hasParameters;
        this.namedOnRight = namedOnRight;
    }

    private static Operator createUnary(int consumeLength, int priority, boolean leftAssociative, boolean pre) {
        Operator operator = new Operator();
        operator.consumeLength = consumeLength;
        operator.priority = priority;
        operator.leftAssociative = leftAssociative;
        operator.pre = pre;
        return operator;
    }

    private static Operator createParameterUnary(int consumeLength, int priority, boolean leftAssociative, boolean exactlyOneParameter,
                                                 String parametersClosing) {
        Operator operator = new Operator();
        operator.consumeLength = consumeLength;
        operator.priority = priority;
        operator.leftAssociative = leftAssociative;
        operator.exactlyOneParameter = exactlyOneParameter;
        operator.hasParameters = true;
        operator.parametersClosing = parametersClosing;
        return operator;
    }

    private static Operator createBinaryLeftAssociative(int consumeLength, int priority) {
        Operator operator = new Operator();
        operator.consumeLength = consumeLength;
        operator.priority = priority;
        operator.leftAssociative = true;
        operator.binary = true;
        return operator;
    }

    private static Operator createBinaryRightAssociative(int consumeLength, int priority) {
        Operator operator = new Operator();
        operator.consumeLength = consumeLength;
        operator.priority = priority;
        operator.leftAssociative = false;
        operator.binary = true;
        return operator;
    }

    public boolean isPre() {
        return pre;
    }

    public boolean isNamedOnRight() {
        return namedOnRight;
    }

    public int getConsumeLength() {
        return consumeLength;
    }

    public boolean isLeftAssociative() {
        return leftAssociative;
    }

    public int getPriority() {
        return priority;
    }

    public boolean isBinary() {
        return binary;
    }

    public boolean isHasParameters() {
        return hasParameters;
    }

    public boolean exactlyOneParameter() {
        return exactlyOneParameter;
    }

    public String getParametersClosing() {
        return parametersClosing;
    }
}
