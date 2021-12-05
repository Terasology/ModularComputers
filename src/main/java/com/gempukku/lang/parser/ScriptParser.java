// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package com.gempukku.lang.parser;

import com.gempukku.lang.DefiningExecutableStatement;
import com.gempukku.lang.ExecutableStatement;
import com.gempukku.lang.IllegalSyntaxException;
import com.gempukku.lang.LangDefinition;
import com.gempukku.lang.Operator;
import com.gempukku.lang.ScriptExecutable;
import com.gempukku.lang.Variable;
import com.gempukku.lang.statement.AddStatement;
import com.gempukku.lang.statement.AssignStatement;
import com.gempukku.lang.statement.BlockStatement;
import com.gempukku.lang.statement.BreakStatement;
import com.gempukku.lang.statement.ComparisonStatement;
import com.gempukku.lang.statement.ConstantStatement;
import com.gempukku.lang.statement.DefineAndAssignStatement;
import com.gempukku.lang.statement.DefineFunctionStatement;
import com.gempukku.lang.statement.DefineStatement;
import com.gempukku.lang.statement.ForStatement;
import com.gempukku.lang.statement.FunctionCallStatement;
import com.gempukku.lang.statement.FunctionStatement;
import com.gempukku.lang.statement.IfStatement;
import com.gempukku.lang.statement.IncrementDecrementStatement;
import com.gempukku.lang.statement.ListDefineStatement;
import com.gempukku.lang.statement.LogicalOperatorStatement;
import com.gempukku.lang.statement.MapAccessStatement;
import com.gempukku.lang.statement.MapDefineStatement;
import com.gempukku.lang.statement.MathStatement;
import com.gempukku.lang.statement.MemberAccessStatement;
import com.gempukku.lang.statement.NamedStatement;
import com.gempukku.lang.statement.NegateStatement;
import com.gempukku.lang.statement.NegativeStatement;
import com.gempukku.lang.statement.ReturnStatement;
import com.gempukku.lang.statement.VariableStatement;
import com.gempukku.lang.statement.WhileStatement;
import com.google.common.collect.Iterators;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class ScriptParser {
    public ScriptExecutable parseScript(Reader reader, Set<String> preDefinedVariables, ScriptParsingCallback scriptParsingCallback)
            throws IllegalSyntaxException, IOException {
        DefinedVariables definedVariables = new DefinedVariables();
        for (String preDefinedVariable : preDefinedVariables) {
            definedVariables.addDefinedVariable(preDefinedVariable);
        }

        BufferedReader bufferedReader = new BufferedReader(reader);

        ScriptExecutable result = new ScriptExecutable();
        List<Term> terms = parseToTerms(bufferedReader, scriptParsingCallback);

        TermBlock termBlockStructure = constructBlocks(terms);

        List<ExecutableStatement> statements = seekStatementsInBlock(termBlockStructure, definedVariables, scriptParsingCallback);
        result.setStatement(new BlockStatement(statements, false, true));

        return result;
    }

    public ScriptExecutable parseScript(Reader reader, Set<String> preDefinedVariables) throws IllegalSyntaxException, IOException {
        return parseScript(reader, preDefinedVariables, null);
    }

    public ScriptExecutable parseScript(Reader reader) throws IllegalSyntaxException, IOException {
        return parseScript(reader, new HashSet<String>());
    }

    private List<ExecutableStatement> seekStatementsInBlock(TermBlock termBlock, DefinedVariables definedVariables,
                                                            ScriptParsingCallback scriptParsingCallback) throws IllegalSyntaxException {
        if (termBlock.isTerm()) {
            throw new IllegalSyntaxException(termBlock.getTerm(), "Expression expected");
        } else {
            List<ExecutableStatement> result = new LinkedList<ExecutableStatement>();
            List<TermBlock> blocks = termBlock.getTermBlocks();
            LastPeekingIterator<TermBlock> termBlockIter = new LastPeekingIterator<TermBlock>(Iterators.peekingIterator(blocks.iterator()));
            while (termBlockIter.hasNext()) {
                if (termBlockIter.peek().isTerm() && termBlockIter.peek().getTerm().getValue().length() == 0) {
                    termBlockIter.next();
                } else {
                    final ExecutableStatement resultStatement = produceStatementFromIterator(termBlockIter, definedVariables,
                            scriptParsingCallback);
                    result.add(resultStatement);
                    if (resultStatement.requiresSemicolon()) {
                        consumeSemicolon(termBlockIter);
                    }
                }
            }
            return result;
        }
    }

    private ExecutableStatement produceStatementFromIterator(LastPeekingIterator<TermBlock> termIterator,
                                                             DefinedVariables definedVariables,
                                                             ScriptParsingCallback scriptParsingCallback) throws IllegalSyntaxException {
        TermBlock firstTermBlock = peekNextTermBlockSafely(termIterator);
        if (firstTermBlock.isTerm()) {
            Term firstTerm = firstTermBlock.getTerm();
            if (firstTerm.getType() == Term.Type.STRING) {
                // We do not allow at the moment any statements starting with constant
                throw new IllegalSyntaxException(firstTerm, "Illegal start of statement");
            } else {
                // It's a program term
                final String value = firstTerm.getValue();
                if (value.length() == 0) {
                    throw new IllegalSyntaxException(firstTerm, "Expression expected");
                }

                String literal = getFirstLiteral(firstTerm);
                if (literal.equals("return")) {
                    makeCallback(scriptParsingCallback, firstTerm, ScriptParsingCallback.Type.KEYWORD, 6);
                    return produceReturnStatement(termIterator, definedVariables, scriptParsingCallback);
                } else if (literal.equals("var")) {
                    makeCallback(scriptParsingCallback, firstTerm, ScriptParsingCallback.Type.KEYWORD, 3);
                    return produceVarStatement(termIterator, definedVariables, scriptParsingCallback);
                } else if (literal.equals("function")) {
                    makeCallback(scriptParsingCallback, firstTerm, ScriptParsingCallback.Type.KEYWORD, 8);
                    return produceDefineFunctionStatement(termIterator, definedVariables, scriptParsingCallback);
                } else if (literal.equals("if")) {
                    makeCallback(scriptParsingCallback, firstTerm, ScriptParsingCallback.Type.KEYWORD, 2);
                    return produceIfStatement(termIterator, definedVariables, scriptParsingCallback);
                } else if (literal.equals("for")) {
                    makeCallback(scriptParsingCallback, firstTerm, ScriptParsingCallback.Type.KEYWORD, 3);
                    return produceForStatement(termIterator, definedVariables, scriptParsingCallback);
                } else if (literal.equals("while")) {
                    makeCallback(scriptParsingCallback, firstTerm, ScriptParsingCallback.Type.KEYWORD, 5);
                    return produceWhileStatement(termIterator, definedVariables, scriptParsingCallback);
                } else if (literal.equals("break")) {
                    makeCallback(scriptParsingCallback, firstTerm, ScriptParsingCallback.Type.KEYWORD, 5);
                    return produceBreakStatement(termIterator);
                } else {
                    return produceExpressionFromIterator(termIterator, definedVariables, false, scriptParsingCallback);
                }
            }
        } else {
            definedVariables.pushNewContext();
            try {
                return new BlockStatement(seekStatementsInBlock(firstTermBlock, definedVariables, scriptParsingCallback), true, false);
            } finally {
                definedVariables.popContext();
            }
        }
    }

    private void makeCallback(ScriptParsingCallback scriptParsingCallback, Term firstTerm, ScriptParsingCallback.Type type, int length) {
        if (scriptParsingCallback != null) {
            scriptParsingCallback.parsed(firstTerm.getLine(), firstTerm.getColumn(), length, type);
        }
    }

    private void makeCallback(ScriptParsingCallback scriptParsingCallback, int line, int column, int length,
                              ScriptParsingCallback.Type type) {
        if (scriptParsingCallback != null) {
            scriptParsingCallback.parsed(line, column, length, type);
        }
    }

    private ExecutableStatement produceBreakStatement(LastPeekingIterator<TermBlock> termIterator) {
        consumeCharactersFromTerm(termIterator, 5);
        return new BreakStatement();
    }

    private ExecutableStatement produceWhileStatement(LastPeekingIterator<TermBlock> termIterator, DefinedVariables definedVariables,
                                                      ScriptParsingCallback scriptParsingCallback) throws IllegalSyntaxException {
        int line = termIterator.peek().getTerm().getLine();

        consumeCharactersFromTerm(termIterator, 5);

        validateNextTermStartingWith(termIterator, "(");
        consumeCharactersFromTerm(termIterator, 1);

        ExecutableStatement condition = produceExpressionFromIterator(termIterator, definedVariables, true, scriptParsingCallback);

        validateNextTermStartingWith(termIterator, ")");
        consumeCharactersFromTerm(termIterator, 1);

        ExecutableStatement statementInLoop = produceStatementFromGroupOrTerm(termIterator, definedVariables, scriptParsingCallback);

        return new WhileStatement(line, condition, statementInLoop);
    }

    private ExecutableStatement produceForStatement(LastPeekingIterator<TermBlock> termIterator, DefinedVariables definedVariables,
                                                    ScriptParsingCallback scriptParsingCallback) throws IllegalSyntaxException {
        int line = termIterator.peek().getTerm().getLine();

        consumeCharactersFromTerm(termIterator, 3);

        validateNextTermStartingWith(termIterator, "(");
        consumeCharactersFromTerm(termIterator, 1);

        definedVariables.pushNewContext();
        try {
            ExecutableStatement firstStatement = null;
            if (!isNextTermStartingWith(termIterator, ";")) {
                firstStatement = produceStatementFromIterator(termIterator, definedVariables, scriptParsingCallback);
            }
            consumeSemicolon(termIterator);

            final ExecutableStatement terminationCondition = produceExpressionFromIterator(termIterator, definedVariables, true,
                    scriptParsingCallback);
            consumeSemicolon(termIterator);

            ExecutableStatement statementExecutedAfterEachLoop = null;
            if (!isNextTermStartingWith(termIterator, ")")) {
                statementExecutedAfterEachLoop = produceStatementFromIterator(termIterator, definedVariables, scriptParsingCallback);
            }

            validateNextTermStartingWith(termIterator, ")");
            consumeCharactersFromTerm(termIterator, 1);

            final ExecutableStatement statementInLoop = produceStatementFromGroupOrTerm(termIterator, definedVariables,
                    scriptParsingCallback);

            return new ForStatement(line, firstStatement, terminationCondition, statementExecutedAfterEachLoop, statementInLoop);
        } finally {
            definedVariables.popContext();
        }
    }

    private ExecutableStatement produceIfStatement(LastPeekingIterator<TermBlock> termIterator, DefinedVariables definedVariables,
                                                   ScriptParsingCallback scriptParsingCallback) throws IllegalSyntaxException {
        int line = termIterator.peek().getTerm().getLine();

        consumeCharactersFromTerm(termIterator, 2);

        ExecutableStatement condition = produceConditionInBrackets(termIterator, definedVariables, scriptParsingCallback);

        ExecutableStatement statement = produceStatementFromGroupOrTerm(termIterator, definedVariables, scriptParsingCallback);
        IfStatement ifStatement = new IfStatement(line, condition, statement);

        boolean hasElse = false;

        while (!hasElse && isNextLiteral(termIterator, "else")) {
            consumeCharactersFromTerm(termIterator, 4);
            if (isNextLiteral(termIterator, "if")) {
                consumeCharactersFromTerm(termIterator, 2);
                ExecutableStatement elseIfCondition = produceConditionInBrackets(termIterator, definedVariables, scriptParsingCallback);
                ifStatement.addElseIf(elseIfCondition, produceStatementFromGroupOrTerm(termIterator, definedVariables,
                        scriptParsingCallback));
            } else {
                ifStatement.addElse(produceStatementFromGroupOrTerm(termIterator, definedVariables, scriptParsingCallback));
                hasElse = true;
            }
        }

        return ifStatement;
    }

    private ExecutableStatement produceStatementFromGroupOrTerm(LastPeekingIterator<TermBlock> termIterator,
                                                                DefinedVariables definedVariables,
                                                                ScriptParsingCallback scriptParsingCallback) throws IllegalSyntaxException {
        ExecutableStatement statement;
        final TermBlock termBlock = peekNextTermBlockSafely(termIterator);
        if (termBlock.isTerm()) {
            if (isNextTermStartingWith(termIterator, ";")) {
                consumeSemicolon(termIterator);
                return null;
            }
            statement = produceStatementFromIterator(termIterator, definedVariables, scriptParsingCallback);
            consumeSemicolon(termIterator);
        } else {
            definedVariables.pushNewContext();
            try {
                termIterator.next();
                final List<ExecutableStatement> statements = seekStatementsInBlock(termBlock, definedVariables, scriptParsingCallback);
                statement = new BlockStatement(statements, false, false);
            } finally {
                definedVariables.popContext();
            }
        }
        return statement;
    }

    private ExecutableStatement produceConditionInBrackets(LastPeekingIterator<TermBlock> termIterator, DefinedVariables definedVariables,
                                                           ScriptParsingCallback scriptParsingCallback) throws IllegalSyntaxException {
        validateNextTermStartingWith(termIterator, "(");
        consumeCharactersFromTerm(termIterator, 1);

        ExecutableStatement condition = produceExpressionFromIterator(termIterator, definedVariables, true, scriptParsingCallback);

        validateNextTermStartingWith(termIterator, ")");
        consumeCharactersFromTerm(termIterator, 1);
        return condition;
    }

    private boolean isNextLiteral(LastPeekingIterator<TermBlock> termIterator, String literal) throws IllegalSyntaxException {
        if (isNextTermStartingWith(termIterator, literal)) {
            final Term term = peekNextProgramTermSafely(termIterator);
            return getFirstLiteral(term).equals(literal);
        }
        return false;
    }

    private DefiningExecutableStatement produceDefineFunctionStatement(LastPeekingIterator<TermBlock> termIterator,
                                                                       DefinedVariables definedVariables,
                                                                       ScriptParsingCallback scriptParsingCallback)
            throws IllegalSyntaxException {
        consumeCharactersFromTerm(termIterator, 8);
        Term functionDefTerm = peekNextProgramTermSafely(termIterator);

        String functionName = getFirstLiteral(functionDefTerm);
        if (LangDefinition.isReservedWord(functionName)) {
            throw new IllegalSyntaxException(functionDefTerm, "Invalid function name");
        }
        if (definedVariables.isVariableDefinedInSameScope(functionName)) {
            throw new IllegalSyntaxException(functionDefTerm, "Variable already defined");
        }
        consumeCharactersFromTerm(termIterator, functionName.length());

        definedVariables.addDefinedVariable(functionName);

        validateNextTermStartingWith(termIterator, "(");
        consumeCharactersFromTerm(termIterator, 1);

        List<String> parameterNames = new ArrayList<String>();
        while (!isNextTermStartingWith(termIterator, ")")) {
            String parameterName = getFirstLiteral(functionDefTerm);
            consumeCharactersFromTerm(termIterator, parameterName.length());
            parameterNames.add(parameterName);
            if (isNextTermStartingWith(termIterator, ",")) {
                consumeCharactersFromTerm(termIterator, 1);
            }
        }
        consumeCharactersFromTerm(termIterator, 1);

        if (!termIterator.hasNext()) {
            throw new IllegalSyntaxException(termIterator, "{ expected");
        }
        final TermBlock functionBodyBlock = termIterator.next();
        if (functionBodyBlock.isTerm()) {
            throw new IllegalSyntaxException(termIterator, "{ expected");
        }

        definedVariables.pushNewContext();
        try {
            for (String parameterName : parameterNames) {
                definedVariables.addDefinedVariable(parameterName);
            }

            final List<ExecutableStatement> functionBody = seekStatementsInBlock(functionBodyBlock, definedVariables,
                    scriptParsingCallback);
            return new DefineFunctionStatement(functionName, parameterNames, functionBody);
        } finally {
            definedVariables.popContext();
        }
    }

    private DefiningExecutableStatement produceVarStatement(LastPeekingIterator<TermBlock> termIterator,
                                                            DefinedVariables definedVariables,
                                                            ScriptParsingCallback scriptParsingCallback) throws IllegalSyntaxException {
        consumeCharactersFromTerm(termIterator, 3);
        final Term variableTerm = peekNextProgramTermSafely(termIterator);
        String variableName = getFirstLiteral(variableTerm);
        int line = variableTerm.getLine();
        int column = variableTerm.getColumn();
        if (LangDefinition.isReservedWord(variableName)) {
            throw new IllegalSyntaxException(variableTerm, "Invalid variable name");
        }
        if (definedVariables.isVariableDefinedInSameScope(variableName)) {
            throw new IllegalSyntaxException(variableTerm, "Variable already defined");
        }

        consumeCharactersFromTerm(termIterator, variableName.length());

        definedVariables.addDefinedVariable(variableName);

        if (isNextTermStartingWith(termIterator, ";")) {
            makeCallback(scriptParsingCallback, line, column, variableName.length(), ScriptParsingCallback.Type.VARIABLE);
            return new DefineStatement(variableName);
        }

        validateNextTermStartingWith(termIterator, "=");

        consumeCharactersFromTerm(termIterator, 1);

        makeCallback(scriptParsingCallback, line, column, variableName.length(), ScriptParsingCallback.Type.VARIABLE);

        final ExecutableStatement value = produceExpressionFromIterator(termIterator, definedVariables, true, scriptParsingCallback);
        return new DefineAndAssignStatement(variableName, value);
    }

    private ExecutableStatement produceReturnStatement(LastPeekingIterator<TermBlock> termIterator, DefinedVariables definedVariables,
                                                       ScriptParsingCallback scriptParsingCallback) throws IllegalSyntaxException {
        consumeCharactersFromTerm(termIterator, 6);
        if (isNextTermStartingWith(termIterator, ";")) {
            return new ReturnStatement(new ConstantStatement(new Variable(null)));
        }
        return new ReturnStatement(produceExpressionFromIterator(termIterator, definedVariables, true, scriptParsingCallback));
    }

    private void consumeCharactersFromTerm(LastPeekingIterator<TermBlock> termIterator, int charCount) {
        final Term term = termIterator.peek().getTerm();
        String termText = term.getValue();
        int previousLength = termText.length();
        String termRemainder = termText.substring(charCount).trim();
        if (termRemainder.length() > 0) {
            term.setValue(termRemainder, previousLength - termRemainder.length());
        } else {
            termIterator.next();
        }
    }

    private void consumeSemicolon(LastPeekingIterator<TermBlock> termIterator) throws IllegalSyntaxException {
        Term term = peekNextProgramTermSafely(termIterator);
        String value = term.getValue();
        if (!value.startsWith(";")) {
            throw new IllegalSyntaxException(term, "; expected");
        }
        consumeCharactersFromTerm(termIterator, 1);
    }

    private ExecutableStatement produceExpressionFromIterator(LastPeekingIterator<TermBlock> termIterator,
                                                              DefinedVariables definedVariables, boolean acceptsVariable,
                                                              ScriptParsingCallback scriptParsingCallback) throws IllegalSyntaxException {
        if (isNextTermStartingWith(termIterator, "[") && acceptsVariable) {
            consumeCharactersFromTerm(termIterator, 1);
            return produceListDefinitionFromIterator(termIterator, definedVariables, scriptParsingCallback);
        }

        int line = getLine(termIterator);

        final ExecutableStatement executableStatement = parseExpression(line, termIterator, definedVariables,
                parseNextOperationToken(termIterator, definedVariables, scriptParsingCallback), Integer.MAX_VALUE, scriptParsingCallback);
        if (!acceptsVariable && executableStatement instanceof VariableStatement) {
            throw new IllegalSyntaxException(termIterator, "Expression expected");
        }
        return executableStatement;
    }

    private int getLine(LastPeekingIterator<TermBlock> termIterator) {
        if (termIterator.hasNext()) {
            final TermBlock termBlock = termIterator.peek();
            if (termBlock.isTerm()) {
                final Term term = termBlock.getTerm();
                return term.getLine();
            } else {
                return termBlock.getBlockStartLine();
            }
        } else {
            final TermBlock lastTermBlock = termIterator.getLast();
            if (lastTermBlock.isTerm()) {
                final Term lastTerm = lastTermBlock.getTerm();
                return lastTerm.getLine();
            } else {
                return lastTermBlock.getBlockEndLine();
            }
        }
    }

    private ExecutableStatement produceListDefinitionFromIterator(LastPeekingIterator<TermBlock> termIterator,
                                                                  DefinedVariables definedVariables,
                                                                  ScriptParsingCallback scriptParsingCallback)
            throws IllegalSyntaxException {
        final List<ExecutableStatement> values = parseParameters(termIterator, definedVariables, false, "]", scriptParsingCallback);
        return new ListDefineStatement(values);
    }

    private ExecutableStatement parseExpression(int line, LastPeekingIterator<TermBlock> termIterator, DefinedVariables definedVariables,
                                                ExecutableStatement left, int maxPriority, ScriptParsingCallback scriptParsingCallback)
            throws IllegalSyntaxException {
        // Based on algorithm from http://en.wikipedia.org/wiki/Operator-precedence_parser on March 28, 2013
        Operator operator;
        ExecutableStatement statLeft = left;
        while ((operator = peekNextOperator(termIterator, statLeft != null)) != null && operator.getPriority() <= maxPriority) {
            if (operator.isBinary()) {
                statLeft = produceBinaryExpression(line, termIterator, definedVariables, statLeft, operator, scriptParsingCallback);
            } else {
                statLeft = produceUnaryExpression(line, termIterator, definedVariables, statLeft, operator, scriptParsingCallback);
            }
        }

        return statLeft;
    }

    private ExecutableStatement produceUnaryExpression(int line, LastPeekingIterator<TermBlock> termIterator,
                                                       DefinedVariables definedVariables, ExecutableStatement left, Operator operator,
                                                       ScriptParsingCallback scriptParsingCallback) throws IllegalSyntaxException {
        consumeCharactersFromTerm(termIterator, operator.getConsumeLength());
        List<ExecutableStatement> parameters = null;
        if (operator.isHasParameters()) {
            parameters = parseParameters(termIterator, definedVariables, operator.exactlyOneParameter(), operator.getParametersClosing(),
                    scriptParsingCallback);
        }

        ExecutableStatement statLeft = left;
        if (operator.isLeftAssociative()) {
            statLeft = produceOperation(line, statLeft, operator, null, parameters);
        } else {
            ExecutableStatement operatorExpression;
            if (operator.isNamedOnRight()) {
                final Term term = peekNextProgramTermSafely(termIterator);
                String literal = getFirstLiteral(term);
                consumeCharactersFromTerm(termIterator, literal.length());
                operatorExpression = new NamedStatement(literal);
            } else {
                operatorExpression = parseExpression(line, termIterator, definedVariables, parseNextOperationToken(termIterator,
                        definedVariables, scriptParsingCallback), operator.getPriority(), scriptParsingCallback);
            }
            if (operator.isPre()) {
                if (operatorExpression == null) {
                    throw new IllegalSyntaxException(termIterator, "Expression expected");
                }
                statLeft = produceOperation(line, operatorExpression, operator, null, parameters);
            } else {
                if (left == null) {
                    throw new IllegalSyntaxException(termIterator, "Expression expected");
                }
                statLeft = produceOperation(line, null, operator, statLeft, parameters);
            }
        }
        return statLeft;
    }

    private ExecutableStatement produceBinaryExpression(int line, LastPeekingIterator<TermBlock> termIterator,
                                                        DefinedVariables definedVariables, ExecutableStatement left, Operator operator,
                                                        ScriptParsingCallback scriptParsingCallback) throws IllegalSyntaxException {
        final Term operatorTerm = termIterator.peek().getTerm();
        int operatorLine = operatorTerm.getLine();
        int operatorColumn = operatorTerm.getColumn();
        consumeCharactersFromTerm(termIterator, operator.getConsumeLength());

        List<ExecutableStatement> parameters = null;
        if (operator.isHasParameters()) {
            parameters = parseParameters(termIterator, definedVariables, operator.exactlyOneParameter(), operator.getParametersClosing(),
                    scriptParsingCallback);
        }

        ExecutableStatement statLeft = left;
        ExecutableStatement right;
        if (operator.isNamedOnRight()) {
            final Term term = peekNextProgramTermSafely(termIterator);
            String literal = getFirstLiteral(term);
            consumeCharactersFromTerm(termIterator, literal.length());
            right = new NamedStatement(literal);
        } else {
            right = parseNextOperationToken(termIterator, definedVariables, scriptParsingCallback);
        }
        if (right == null) {
            throw new IllegalSyntaxException(termIterator, "Expression expected");
        }
        right = produceExpressionOnRightSide(line, termIterator, definedVariables, statLeft, operator, right, scriptParsingCallback);

        if (statLeft == null) {
            throw new IllegalSyntaxException(operatorLine, operatorColumn, "Expression expected");
        }
        if (right == null) {
            throw new IllegalSyntaxException(operatorLine, operatorColumn + operator.getConsumeLength(), "Expression expected");
        }
        statLeft = produceOperation(line, statLeft, operator, right, parameters);
        return statLeft;
    }

    private ExecutableStatement produceExpressionOnRightSide(int line, LastPeekingIterator<TermBlock> termIterator,
                                                             DefinedVariables definedVariables, ExecutableStatement left,
                                                             Operator operator, ExecutableStatement right,
                                                             ScriptParsingCallback scriptParsingCallback) throws IllegalSyntaxException {
        Operator nextOperator;
        ExecutableStatement statRight = right;
        while ((nextOperator = peekNextOperator(termIterator, left != null)) != null
                && (nextOperator.getPriority() < operator.getPriority() || (nextOperator.getPriority() == operator.getPriority()
                && !nextOperator.isLeftAssociative()))) {
            if (operator.isBinary()) {
                statRight = parseExpression(line, termIterator, definedVariables, statRight, nextOperator.getPriority(), scriptParsingCallback);
            } else {
                consumeCharactersFromTerm(termIterator, operator.getConsumeLength());
                if (operator.isPre()) {
                    if (statRight == null) {
                        throw new IllegalSyntaxException(termIterator, "Expression expected");
                    }
                    statRight = produceOperation(line, null, nextOperator, statRight, parseParameters(termIterator, definedVariables,
                            nextOperator.exactlyOneParameter(), nextOperator.getParametersClosing(), scriptParsingCallback));
                } else {
                    if (left == null) {
                        throw new IllegalSyntaxException(termIterator, "Expression expected");
                    }
                    statRight = produceOperation(line, left, nextOperator, null, parseParameters(termIterator, definedVariables,
                            nextOperator.exactlyOneParameter(), nextOperator.getParametersClosing(), scriptParsingCallback));
                }
            }
        }
        return statRight;
    }

    private List<ExecutableStatement> parseParameters(LastPeekingIterator<TermBlock> termIterator, DefinedVariables definedVariables,
                                                      boolean exactlyOneParameter, String parametersClosing,
                                                      ScriptParsingCallback scriptParsingCallback) throws IllegalSyntaxException {
        boolean first = true;
        List<ExecutableStatement> parameters;
        parameters = new ArrayList<ExecutableStatement>();
        while (!isNextTermStartingWith(termIterator, parametersClosing)) {
            if (!first) {
                if (exactlyOneParameter) {
                    throw new IllegalSyntaxException(termIterator, parametersClosing + " expected");
                }
                validateNextTermStartingWith(termIterator, ",");
                consumeCharactersFromTerm(termIterator, 1);
            }

            parameters.add(produceExpressionFromIterator(termIterator, definedVariables, true, scriptParsingCallback));
            first = false;
        }
        if (first && exactlyOneParameter) {
            throw new IllegalSyntaxException(termIterator, parametersClosing + " expected");
        }
        consumeCharactersFromTerm(termIterator, parametersClosing.length());
        return parameters;
    }

    private Operator peekNextOperator(LastPeekingIterator<TermBlock> termIterator, boolean hasLeft) throws IllegalSyntaxException {
        if (!termIterator.hasNext()) {
            return null;
        }
        final Term term = peekNextProgramTermSafely(termIterator);
        String termValue = term.getValue();
        Operator operator = null;
        if (termValue.startsWith("==")) {
            operator = Operator.EQUALS;
        } else if (termValue.startsWith("!=")) {
            operator = Operator.NOT_EQUALS;
        } else if (termValue.startsWith("=")) {
            operator = Operator.ASSIGNMENT;
        } else if (termValue.startsWith("(")) {
            operator = Operator.FUNCTION_CALL;
        } else if (termValue.startsWith("++")) {
            if (hasLeft) {
                operator = Operator.POST_INCREMENT;
            } else {
                operator = Operator.PRE_INCREMENT;
            }
        } else if (termValue.startsWith("--")) {
            if (hasLeft) {
                operator = Operator.POST_DECREMENT;
            } else {
                operator = Operator.PRE_DECREMENT;
            }
        } else if (termValue.startsWith("+=")) {
            operator = Operator.ADD_ASSIGN;
        } else if (termValue.startsWith("-=")) {
            operator = Operator.SUBTRACT_ASSIGN;
        } else if (termValue.startsWith("*=")) {
            operator = Operator.MULTIPLY_ASSIGN;
        } else if (termValue.startsWith("/=")) {
            operator = Operator.DIVIDE_ASSIGN;
        } else if (termValue.startsWith("%=")) {
            operator = Operator.MOD_ASSIGN;
        } else if (termValue.startsWith("+")) {
            operator = Operator.ADD;
        } else if (termValue.startsWith("-")) {
            if (hasLeft) {
                operator = Operator.SUBTRACT;
            } else {
                operator = Operator.NEGATIVE;
            }
        } else if (termValue.startsWith("*")) {
            operator = Operator.MULTIPLY;
        } else if (termValue.startsWith("/")) {
            operator = Operator.DIVIDE;
        } else if (termValue.startsWith("%")) {
            operator = Operator.MOD;
        } else if (termValue.startsWith(">=")) {
            operator = Operator.GREATER_OR_EQUAL;
        } else if (termValue.startsWith(">")) {
            operator = Operator.GREATER;
        } else if (termValue.startsWith("<=")) {
            operator = Operator.LESS_OR_EQUAL;
        } else if (termValue.startsWith("<")) {
            operator = Operator.LESS;
        } else if (termValue.startsWith(".")) {
            operator = Operator.MEMBER_ACCESS;
        } else if (termValue.startsWith("&&")) {
            operator = Operator.AND;
        } else if (termValue.startsWith("||")) {
            operator = Operator.OR;
        } else if (termValue.startsWith("!")) {
            operator = Operator.NOT;
        } else if (termValue.startsWith("[")) {
            operator = Operator.MAPPED_ACCESS;
        }

        return operator;
    }

    private ExecutableStatement produceOperation(int line, ExecutableStatement left, Operator operator, ExecutableStatement right,
                                                 List<ExecutableStatement> parameters) throws IllegalSyntaxException {
        if (operator == Operator.ASSIGNMENT) {
            return new AssignStatement(left, right);
        } else if (operator == Operator.FUNCTION_CALL) {
            return new FunctionCallStatement(line, left, parameters);
        } else if (operator == Operator.ADD) {
            return new AddStatement(line, left, right, false);
        } else if (operator == Operator.ADD_ASSIGN) {
            return new AddStatement(line, left, right, true);
        } else if (operator == Operator.EQUALS || operator == Operator.NOT_EQUALS) {
            return new ComparisonStatement(left, operator, right);
        } else if (operator == Operator.MEMBER_ACCESS) {
            return new MemberAccessStatement(line, left, ((NamedStatement) right).getName());
        } else if (operator == Operator.AND || operator == Operator.OR) {
            return new LogicalOperatorStatement(line, left, operator, right);
        } else if (operator == Operator.NOT) {
            return new NegateStatement(line, left);
        } else if (operator == Operator.NEGATIVE) {
            return new NegativeStatement(line, left);
        } else if (operator == Operator.MAPPED_ACCESS) {
            return new MapAccessStatement(line, left, parameters.get(0));
        } else if (operator == Operator.PRE_INCREMENT || operator == Operator.PRE_DECREMENT) {
            return new IncrementDecrementStatement(line, left, operator == Operator.PRE_INCREMENT, true);
        } else if (operator == Operator.POST_INCREMENT || operator == Operator.POST_DECREMENT) {
            return new IncrementDecrementStatement(line, right, operator == Operator.POST_INCREMENT, false);
        } else if (operator == Operator.ADD_ASSIGN || operator == Operator.SUBTRACT_ASSIGN || operator == Operator.MULTIPLY_ASSIGN
                || operator == Operator.DIVIDE_ASSIGN || operator == Operator.MOD_ASSIGN) {
            return new MathStatement(line, left, operator, right, true);
        } else {
            return new MathStatement(line, left, operator, right, false);
        }
    }

    private ExecutableStatement parseNextOperationToken(LastPeekingIterator<TermBlock> termIterator, DefinedVariables definedVariables,
                                                        ScriptParsingCallback scriptParsingCallback) throws IllegalSyntaxException {
        ExecutableStatement result;
        TermBlock termBlock = peekNextTermBlockSafely(termIterator);
        if (termBlock.isTerm()) {
            Term term = termBlock.getTerm();
            if (term.getType() == Term.Type.STRING) {
                String value = term.getValue();
                result = new ConstantStatement(new Variable(value));
                // Consume the String
                termIterator.next();
            } else {
                int line = term.getLine();
                int column = term.getColumn();
                // PROGRAM term
                String termValue = term.getValue();
                if (termValue.charAt(0) == '(') {
                    consumeCharactersFromTerm(termIterator, 1);
                    result = produceExpressionFromIterator(termIterator, definedVariables, true, scriptParsingCallback);
                    validateNextTermStartingWith(termIterator, ")");
                    consumeCharactersFromTerm(termIterator, 1);
                } else if (Character.isDigit(termValue.charAt(0))) {
                    String numberInStr = getNumber(termValue);
                    consumeCharactersFromTerm(termIterator, numberInStr.length());
                    makeCallback(scriptParsingCallback, line, column, numberInStr.length(), ScriptParsingCallback.Type.CONSTANT);
                    result = new ConstantStatement(new Variable(Float.parseFloat(numberInStr)));
                } else {
                    if (Character.isLetter(termValue.charAt(0))) {
                        String literal = getFirstLiteral(term);

                        consumeCharactersFromTerm(termIterator, literal.length());

                        if (literal.equals("true")) {
                            makeCallback(scriptParsingCallback, line, column, 4, ScriptParsingCallback.Type.CONSTANT);
                            result = new ConstantStatement(new Variable(true));
                        } else if (literal.equals("false")) {
                            makeCallback(scriptParsingCallback, line, column, 5, ScriptParsingCallback.Type.CONSTANT);
                            result = new ConstantStatement(new Variable(false));
                        } else if (literal.equals("null")) {
                            makeCallback(scriptParsingCallback, line, column, 4, ScriptParsingCallback.Type.CONSTANT);
                            result = new ConstantStatement(new Variable(null));
                        } else if (literal.equals("function")) {
                            result = produceFunctionFromIterator(termIterator, definedVariables, term, scriptParsingCallback);
                        } else {
                            if (LangDefinition.isReservedWord(literal)) {
                                throw new IllegalSyntaxException(line, column, "Invalid variable name");
                            }
                            if (!definedVariables.isVariableDefined(literal)) {
                                throw new IllegalSyntaxException(line, column, "Variable " + literal + " not defined in scope");
                            }
                            makeCallback(scriptParsingCallback, line, column, literal.length(), ScriptParsingCallback.Type.VARIABLE);
                            result = new VariableStatement(literal);
                        }
                    } else {
                        // It might be operator
                        result = null;
                    }
                }
            }
        } else {
            // It's a map (in {})
            result = produceMapDefinitionFromBlock(termBlock, definedVariables, scriptParsingCallback);
            // Consume the block
            termIterator.next();
        }
        return result;
    }

    private ExecutableStatement produceFunctionFromIterator(LastPeekingIterator<TermBlock> termIterator,
                                                            DefinedVariables definedVariables, Term term,
                                                            ScriptParsingCallback scriptParsingCallback) throws IllegalSyntaxException {
        ExecutableStatement result;
        validateNextTermStartingWith(termIterator, "(");
        consumeCharactersFromTerm(termIterator, 1);

        List<String> parameterNames = new ArrayList<String>();
        while (!isNextTermStartingWith(termIterator, ")")) {
            String parameterName = getFirstLiteral(term);
            consumeCharactersFromTerm(termIterator, parameterName.length());
            parameterNames.add(parameterName);
            if (isNextTermStartingWith(termIterator, ",")) {
                consumeCharactersFromTerm(termIterator, 1);
            }
        }
        consumeCharactersFromTerm(termIterator, 1);

        if (!termIterator.hasNext()) {
            throw new IllegalSyntaxException(termIterator, "{ expected");
        }
        final TermBlock functionBodyBlock = termIterator.next();
        if (functionBodyBlock.isTerm()) {
            throw new IllegalSyntaxException(termIterator, "{ expected");
        }

        definedVariables.pushNewContext();
        try {
            for (String parameterName : parameterNames) {
                definedVariables.addDefinedVariable(parameterName);
            }

            final List<ExecutableStatement> functionBody = seekStatementsInBlock(functionBodyBlock, definedVariables,
                    scriptParsingCallback);
            result = new FunctionStatement(parameterNames, functionBody);
        } finally {
            definedVariables.popContext();
        }
        return result;
    }

    private ExecutableStatement produceMapDefinitionFromBlock(TermBlock termBlock, DefinedVariables definedVariables,
                                                              ScriptParsingCallback scriptParsingCallback) throws IllegalSyntaxException {
        MapDefineStatement mapStatement = new MapDefineStatement();
        final LastPeekingIterator<TermBlock> iterator =
                new LastPeekingIterator<TermBlock>(Iterators.peekingIterator(termBlock.getTermBlocks().iterator()));
        boolean first = true;
        while (iterator.hasNext()) {
            if (!first) {
                validateNextTermStartingWith(iterator, ",");
                consumeCharactersFromTerm(iterator, 1);
            }
            final TermBlock property = iterator.peek();
            if (!property.isTerm()) {
                throw new IllegalSyntaxException(property.getBlockStartLine(), property.getBlockStartColumn(), "Property name expected");
            }
            String propertyName;
            int propertyLine;
            int propertyColumn;
            if (property.getTerm().getType() == Term.Type.STRING) {
                propertyName = property.getTerm().getValue();
                // Consume the string
                iterator.next();
                propertyLine = property.getTerm().getLine();
                propertyColumn = property.getTerm().getColumn();
            } else {
                propertyName = getFirstLiteral(property.getTerm());
                propertyLine = property.getTerm().getLine();
                propertyColumn = property.getTerm().getColumn();
                consumeCharactersFromTerm(iterator, propertyName.length());
            }

            validateNextTermStartingWith(iterator, ":");
            consumeCharactersFromTerm(iterator, 1);

            mapStatement.addProperty(propertyLine, propertyColumn, propertyName, produceExpressionFromIterator(iterator, definedVariables,
                    true, scriptParsingCallback));

            first = false;
        }
        return mapStatement;
    }

    private String getNumber(String termValue) {
        StringBuilder result = new StringBuilder();
        boolean hasDot = false;
        final char[] chars = termValue.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (i == 0 && chars[i] == '-') {
                result.append(chars[i]);
            } else if (Character.isDigit(chars[i])) {
                result.append(chars[i]);
            } else if (chars[i] == '.' && !hasDot) {
                hasDot = true;
                result.append('.');
            } else {
                return result.toString();
            }
        }
        return result.toString();
    }

    private String getFirstLiteral(Term term) throws IllegalSyntaxException {
        StringBuilder sb = new StringBuilder();
        char[] chars = term.getValue().toCharArray();
        if (!Character.isLetter(chars[0])) {
            throw new IllegalSyntaxException(term, "Expected expression");
        }
        for (char c : chars) {
            if (Character.isLetterOrDigit(c)) {
                sb.append(c);
            } else {
                return sb.toString();
            }
        }
        return sb.toString();
    }

    private Term peekNextProgramTermSafely(LastPeekingIterator<TermBlock> termIterator) throws IllegalSyntaxException {
        if (termIterator.hasNext()) {
            TermBlock termBlock = termIterator.peek();
            if (!termBlock.isTerm()) {
                throw new IllegalSyntaxException(termIterator, "Expression expected");
            }

            Term term = termBlock.getTerm();
            if (term.getType() != Term.Type.PROGRAM) {
                throw new IllegalSyntaxException(termIterator, "Expression expected");
            }
            return term;
        } else {
            throw new IllegalSyntaxException(termIterator, "Expression expected");
        }
    }

    private TermBlock peekNextTermBlockSafely(LastPeekingIterator<TermBlock> termIterator) throws IllegalSyntaxException {
        if (termIterator.hasNext()) {
            return termIterator.peek();
        } else {
            throw new IllegalSyntaxException(termIterator, "Expression expected");
        }
    }

    private void validateNextTermStartingWith(LastPeekingIterator<TermBlock> termIterator, String text) throws IllegalSyntaxException {
        if (!isNextTermStartingWith(termIterator, text)) {
            throw new IllegalSyntaxException(termIterator, text + " expected");
        }
    }

    private boolean isNextTermStartingWith(LastPeekingIterator<TermBlock> termIterator, String text) {
        if (termIterator.hasNext()) {
            final TermBlock termBlock = termIterator.peek();
            if (termBlock.isTerm()) {
                final Term term = termBlock.getTerm();
                if (term.getType() == Term.Type.PROGRAM) {
                    return term.getValue().startsWith(text);
                }
            }
        }
        return false;
    }

    private void printTerms(int indent, TermBlock block) {
        if (block.isTerm()) {
            for (int i = 0; i < indent; i++) {
                System.out.print("  ");
            }
            Term term = block.getTerm();
            System.out.println(term.getType() + "(" + term.getLine() + ", " + term.getColumn() + "):" + term.getValue());
        } else {
            List<TermBlock> childBlocks = block.getTermBlocks();
            for (TermBlock childBlock : childBlocks) {
                printTerms(indent + 1, childBlock);
            }
        }
    }

    private TermBlock constructBlocks(List<Term> terms) throws IllegalSyntaxException {
        LinkedList<TermBlock> termBlocksStack = new LinkedList<TermBlock>();

        TermBlock result = new TermBlock(0, 0);
        TermBlock currentBlock = result;

        for (Term term : terms) {
            if (term.getType() == Term.Type.PROGRAM) {
                String value = term.getValue();
                int columnIncrement = 0;

                while (value.length() > 0) {
                    int open = value.indexOf('{');
                    int close = value.indexOf('}');
                    if (open > -1 && (close < 0 || open < close)) {
                        String before = value.substring(0, open);
                        String after = value.substring(open + 1);
                        if (before.length() > 0) {
                            String beforeTrimmed = before.trim();
                            int newColumn = columnIncrement + term.getColumn() + before.indexOf(beforeTrimmed);
                            appendProgramTerm(currentBlock, beforeTrimmed, term.getLine(), newColumn);
                        }
                        termBlocksStack.add(currentBlock);
                        TermBlock childBlock = new TermBlock(term.getLine(), open);
                        currentBlock.addTermBlock(childBlock);
                        currentBlock = childBlock;
                        value = after;
                        columnIncrement += open + 1;
                    } else if (close > -1 && (open < 0 || close < open)) {
                        String before = value.substring(0, close);
                        String after = value.substring(close + 1);
                        if (before.length() > 0) {
                            String beforeTrimmed = before.trim();
                            int newColumn = columnIncrement + term.getColumn() + before.indexOf(beforeTrimmed);
                            appendProgramTerm(currentBlock, beforeTrimmed, term.getLine(), newColumn);
                        }
                        if (termBlocksStack.size() == 0) {
                            throw new IllegalSyntaxException(term.getLine(), close, "Found closing bracket for no block");
                        }
                        currentBlock.terminateTermBlock(term.getLine(), close);
                        currentBlock = termBlocksStack.removeLast();
                        value = after;
                        columnIncrement += close + 1;
                    } else {
                        String valueTrimmed = value.trim();
                        if (valueTrimmed.length() > 0) {
                            int newColumn = columnIncrement + term.getColumn() + value.indexOf(valueTrimmed);
                            appendProgramTerm(currentBlock, valueTrimmed, term.getLine(), newColumn);
                        }
                        value = "";
                    }
                }
            } else if (term.getType() == Term.Type.STRING) {
                currentBlock.addTermBlock(term);
            }
        }

        if (termBlocksStack.size() > 0) {
            final Term lastTerm = terms.get(terms.size() - 1);
            throw new IllegalSyntaxException(lastTerm.getLine(), lastTerm.getColumn() + lastTerm.getValue().length(), "Unclosed bracket " +
                    "-" + " }");
        }

        return result;
    }

    private void appendProgramTerm(TermBlock currentBlock, String text, int line, int column) {
        currentBlock.addTermBlock(new Term(Term.Type.PROGRAM, text, line, column));
    }

    private List<Term> parseToTerms(BufferedReader bufferedReader, ScriptParsingCallback scriptParsingCallback) throws IOException,
            IllegalSyntaxException {
        int lineNumber = 0;
        List<Term> terms = new ArrayList<Term>();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            termAndValidateLine(line, lineNumber, terms, scriptParsingCallback);
            lineNumber++;
        }
        return terms;
    }

    // TODO: This needs to be removed again - but to do so the below logic needs cleaning up as there's an intermediate i++ buried in it.
    @SuppressWarnings("checkstyle:ModifiedControlVariable")
    private void termAndValidateLine(String line, int lineNumber, List<Term> resultTerms, ScriptParsingCallback scriptParsingCallback)
            throws IllegalSyntaxException {
        // Remove all not needed white-space characters
        Term.Type type = Term.Type.PROGRAM;
        StringBuilder valueSoFar = new StringBuilder();
        char[] lineChars = line.toCharArray();
        int termStartColumn = 0;
        for (int i = 0; i < lineChars.length; i++) {
            if (type == Term.Type.PROGRAM) {
                if (lineChars[i] == '\"') {
                    if (valueSoFar.length() > 0) {
                        resultTerms.add(new Term(type, valueSoFar.toString(), lineNumber, termStartColumn));
                    }
                    type = Term.Type.STRING;
                    valueSoFar = new StringBuilder();
                    termStartColumn = i + 1;
                } else if (lineChars[i] == '/' && i + 1 < lineChars.length && lineChars[i + 1] == '/') {
                    if (valueSoFar.length() > 0) {
                        resultTerms.add(new Term(type, valueSoFar.toString(), lineNumber, termStartColumn));
                    }
                    type = Term.Type.COMMENT;
                    valueSoFar = new StringBuilder();
                    termStartColumn = i;
                } else {
                    valueSoFar.append(lineChars[i]);
                }
            } else if (type == Term.Type.STRING) {
                if (lineChars[i] == '\"') {
                    makeCallback(scriptParsingCallback, lineNumber, termStartColumn - 1, valueSoFar.length() + 2,
                            ScriptParsingCallback.Type.LITERAL);
                    resultTerms.add(new Term(type, valueSoFar.toString(), lineNumber, termStartColumn));
                    type = Term.Type.PROGRAM;
                    valueSoFar = new StringBuilder();
                    termStartColumn = i + 1;
                } else if (lineChars[i] == '\\') {
                    i++;
                    if (i < lineChars.length) {
                        if (lineChars[i] == '\"' || lineChars[i] == '\\') {
                            valueSoFar.append(lineChars[i]);
                        } else {
                            throw new IllegalSyntaxException(lineNumber, i, "Illegal escape sequence in String \\" + lineChars[i]);
                        }
                    } else {
                        throw new IllegalSyntaxException(lineNumber, i, "Unfinished escape sequence in String");
                    }
                } else {
                    valueSoFar.append(lineChars[i]);
                }
            } else {
                valueSoFar.append(lineChars[i]);
            }
        }

        if (valueSoFar.length() > 0) {
            if (type == Term.Type.COMMENT) {
                makeCallback(scriptParsingCallback, lineNumber, termStartColumn, valueSoFar.length() + 1,
                        ScriptParsingCallback.Type.COMMENT);
            }

            resultTerms.add(new Term(type, valueSoFar.toString(), lineNumber, termStartColumn));
        }
    }
}
