package com.gempukku.lang.parser;

import com.gempukku.lang.*;
import com.gempukku.lang.statement.*;
import com.google.common.collect.Iterators;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.*;

public class ScriptParser {
    public ScriptExecutable parseScript(Reader reader, Set<String> preDefinedVariables) throws IllegalSyntaxException, IOException {
        DefinedVariables definedVariables = new DefinedVariables();
        for (String preDefinedVariable : preDefinedVariables)
            definedVariables.addDefinedVariable(preDefinedVariable);

        BufferedReader bufferedReader = new BufferedReader(reader);

        ScriptExecutable result = new ScriptExecutable();
        List<Term> terms = parseToTerms(bufferedReader);

//		for (Term term : terms)
//			System.out.println(term.getType() + "(" + term.getLine() + ", " + term.getColumn() + "):" + term.getValue());

        TermBlock termBlockStructure = constructBlocks(terms);

//		System.out.println("Printing program structure");
//		printTerms(0, termBlockStructure);

        List<ExecutableStatement> statements = seekStatementsInBlock(termBlockStructure, definedVariables);
        result.setStatement(new BlockStatement(statements, false, true));

        return result;
    }

    public ScriptExecutable parseScript(Reader reader) throws IllegalSyntaxException, IOException {
        return parseScript(reader, new HashSet<String>());
    }

    private List<ExecutableStatement> seekStatementsInBlock(TermBlock termBlock, DefinedVariables definedVariables) throws IllegalSyntaxException {
        if (termBlock.isTerm()) {
            throw new IllegalSyntaxException(termBlock.getTerm(), "Expression expected");
        } else {
            List<ExecutableStatement> result = new LinkedList<ExecutableStatement>();
            List<TermBlock> blocks = termBlock.getTermBlocks();
            LastPeekingIterator<TermBlock> termBlockIter = new LastPeekingIterator<TermBlock>(Iterators.peekingIterator(blocks.iterator()));
            while (termBlockIter.hasNext()) {
                if (termBlockIter.peek().isTerm() && termBlockIter.peek().getTerm().getValue().length() == 0)
                    termBlockIter.next();
                else {
                    final ExecutableStatement resultStatement = produceStatementFromIterator(termBlockIter, definedVariables);
                    result.add(resultStatement);
                    if (resultStatement.requiresSemicolon())
                        consumeSemicolon(termBlockIter);
                }
            }
            return result;
        }
    }

    private ExecutableStatement produceStatementFromIterator(LastPeekingIterator<TermBlock> termIterator, DefinedVariables definedVariables) throws IllegalSyntaxException {
        TermBlock firstTermBlock = peekNextTermBlockSafely(termIterator);
        if (firstTermBlock.isTerm()) {
            Term firstTerm = firstTermBlock.getTerm();
            if (firstTerm.getType() == Term.Type.STRING) {
                // We do not allow at the moment any statements starting with constant
                throw new IllegalSyntaxException(firstTerm, "Illegal start of statement");
            } else {
                // It's a program term
                final String value = firstTerm.getValue();
                if (value.length() == 0)
                    throw new IllegalSyntaxException(firstTerm, "Expression expected");

                String literal = getFirstLiteral(firstTerm);
                if (literal.equals("return")) {
                    return produceReturnStatement(termIterator, definedVariables);
                } else if (literal.equals("var")) {
                    return produceVarStatement(termIterator, definedVariables);
                } else if (literal.equals("function")) {
                    return produceDefineFunctionStatement(termIterator, definedVariables);
                } else if (literal.equals("if")) {
                    return produceIfStatement(termIterator, definedVariables);
                } else if (literal.equals("for")) {
                    return produceForStatement(termIterator, definedVariables);
                } else if (literal.equals("while")) {
                    return produceWhileStatement(termIterator, definedVariables);
                } else if (literal.equals("break")) {
                    return produceBreakStatement(termIterator);
                } else {
                    return produceExpressionFromIterator(termIterator, definedVariables, false);
                }
            }
        } else {
            definedVariables.pushNewContext();
            try {
                return new BlockStatement(seekStatementsInBlock(firstTermBlock, definedVariables), true, false);
            } finally {
                definedVariables.popContext();
            }
        }
    }

    private ExecutableStatement produceBreakStatement(LastPeekingIterator<TermBlock> termIterator) {
        consumeCharactersFromTerm(termIterator, 5);
        return new BreakStatement();
    }

    private ExecutableStatement produceWhileStatement(LastPeekingIterator<TermBlock> termIterator, DefinedVariables definedVariables) throws IllegalSyntaxException {
        int line = termIterator.peek().getTerm().getLine();

        consumeCharactersFromTerm(termIterator, 5);

        validateNextTermStartingWith(termIterator, "(");
        consumeCharactersFromTerm(termIterator, 1);

        ExecutableStatement condition = produceExpressionFromIterator(termIterator, definedVariables, true);

        validateNextTermStartingWith(termIterator, ")");
        consumeCharactersFromTerm(termIterator, 1);

        ExecutableStatement statementInLoop = produceStatementFromGroupOrTerm(termIterator, definedVariables);

        return new WhileStatement(line, condition, statementInLoop);
    }

    private ExecutableStatement produceForStatement(LastPeekingIterator<TermBlock> termIterator, DefinedVariables definedVariables) throws IllegalSyntaxException {
        int line = termIterator.peek().getTerm().getLine();

        consumeCharactersFromTerm(termIterator, 3);

        validateNextTermStartingWith(termIterator, "(");
        consumeCharactersFromTerm(termIterator, 1);

        definedVariables.pushNewContext();
        try {
            ExecutableStatement firstStatement = null;
            if (!isNextTermStartingWith(termIterator, ";"))
                firstStatement = produceStatementFromIterator(termIterator, definedVariables);
            consumeSemicolon(termIterator);

            final ExecutableStatement terminationCondition = produceExpressionFromIterator(termIterator, definedVariables, true);
            consumeSemicolon(termIterator);

            ExecutableStatement statementExecutedAfterEachLoop = null;
            if (!isNextTermStartingWith(termIterator, ")"))
                statementExecutedAfterEachLoop = produceStatementFromIterator(termIterator, definedVariables);

            validateNextTermStartingWith(termIterator, ")");
            consumeCharactersFromTerm(termIterator, 1);

            final ExecutableStatement statementInLoop = produceStatementFromGroupOrTerm(termIterator, definedVariables);

            return new ForStatement(line, firstStatement, terminationCondition, statementExecutedAfterEachLoop, statementInLoop);
        } finally {
            definedVariables.popContext();
        }
    }

    private ExecutableStatement produceIfStatement(LastPeekingIterator<TermBlock> termIterator, DefinedVariables definedVariables) throws IllegalSyntaxException {
        int line = termIterator.peek().getTerm().getLine();

        consumeCharactersFromTerm(termIterator, 2);

        ExecutableStatement condition = produceConditionInBrackets(termIterator, definedVariables);

        ExecutableStatement statement = produceStatementFromGroupOrTerm(termIterator, definedVariables);
        IfStatement ifStatement = new IfStatement(line, condition, statement);

        boolean hasElse = false;

        while (!hasElse && isNextLiteral(termIterator, "else")) {
            consumeCharactersFromTerm(termIterator, 4);
            if (isNextLiteral(termIterator, "if")) {
                consumeCharactersFromTerm(termIterator, 2);
                ExecutableStatement elseIfCondition = produceConditionInBrackets(termIterator, definedVariables);
                ifStatement.addElseIf(elseIfCondition, produceStatementFromGroupOrTerm(termIterator, definedVariables));
            } else {
                ifStatement.addElse(produceStatementFromGroupOrTerm(termIterator, definedVariables));
                hasElse = true;
            }
        }

        return ifStatement;
    }

    private ExecutableStatement produceStatementFromGroupOrTerm(LastPeekingIterator<TermBlock> termIterator, DefinedVariables definedVariables) throws IllegalSyntaxException {
        ExecutableStatement statement;
        final TermBlock termBlock = peekNextTermBlockSafely(termIterator);
        if (termBlock.isTerm()) {
            if (isNextTermStartingWith(termIterator, ";")) {
                consumeSemicolon(termIterator);
                return null;
            }
            statement = produceStatementFromIterator(termIterator, definedVariables);
            consumeSemicolon(termIterator);
        } else {
            definedVariables.pushNewContext();
            try {
                termIterator.next();
                final List<ExecutableStatement> statements = seekStatementsInBlock(termBlock, definedVariables);
                statement = new BlockStatement(statements, false, false);
            } finally {
                definedVariables.popContext();
            }
        }
        return statement;
    }

    private ExecutableStatement produceConditionInBrackets(LastPeekingIterator<TermBlock> termIterator, DefinedVariables definedVariables) throws IllegalSyntaxException {
        validateNextTermStartingWith(termIterator, "(");
        consumeCharactersFromTerm(termIterator, 1);

        ExecutableStatement condition = produceExpressionFromIterator(termIterator, definedVariables, true);

        validateNextTermStartingWith(termIterator, ")");
        consumeCharactersFromTerm(termIterator, 1);
        return condition;
    }

    private boolean isNextLiteral(LastPeekingIterator<TermBlock> termIterator, String literal) throws IllegalSyntaxException {
        if (isNextTermStartingWith(termIterator, literal)) {
            final Term term = peekNextProgramTermSafely(termIterator);
            if (getFirstLiteral(term).equals(literal))
                return true;
        }
        return false;
    }

    private DefiningExecutableStatement produceDefineFunctionStatement(LastPeekingIterator<TermBlock> termIterator, DefinedVariables definedVariables) throws IllegalSyntaxException {
        consumeCharactersFromTerm(termIterator, 8);
        Term functionDefTerm = peekNextProgramTermSafely(termIterator);

        String functionName = getFirstLiteral(functionDefTerm);
        if (LangDefinition.isReservedWord(functionName))
            throw new IllegalSyntaxException(functionDefTerm, "Invalid function name");
        if (definedVariables.isVariableDefinedInSameScope(functionName))
            throw new IllegalSyntaxException(functionDefTerm, "Variable already defined");
        consumeCharactersFromTerm(termIterator, functionName.length());

        definedVariables.addDefinedVariable(functionName);

        validateNextTermStartingWith(termIterator, "(");
        consumeCharactersFromTerm(termIterator, 1);

        List<String> parameterNames = new ArrayList<String>();
        while (!isNextTermStartingWith(termIterator, ")")) {
            String parameterName = getFirstLiteral(functionDefTerm);
            consumeCharactersFromTerm(termIterator, parameterName.length());
            parameterNames.add(parameterName);
            if (isNextTermStartingWith(termIterator, ","))
                consumeCharactersFromTerm(termIterator, 1);
        }
        consumeCharactersFromTerm(termIterator, 1);

        if (!termIterator.hasNext())
            throw new IllegalSyntaxException(termIterator, "{ expected");
        final TermBlock functionBodyBlock = termIterator.next();
        if (functionBodyBlock.isTerm())
            throw new IllegalSyntaxException(termIterator, "{ expected");

        definedVariables.pushNewContext();
        try {
            for (String parameterName : parameterNames)
                definedVariables.addDefinedVariable(parameterName);

            final List<ExecutableStatement> functionBody = seekStatementsInBlock(functionBodyBlock, definedVariables);
            return new DefineFunctionStatement(functionName, parameterNames, functionBody);
        } finally {
            definedVariables.popContext();
        }
    }

    private DefiningExecutableStatement produceVarStatement(LastPeekingIterator<TermBlock> termIterator, DefinedVariables definedVariables) throws IllegalSyntaxException {
        consumeCharactersFromTerm(termIterator, 3);
        final Term variableTerm = peekNextProgramTermSafely(termIterator);
        String variableName = getFirstLiteral(variableTerm);
        if (LangDefinition.isReservedWord(variableName))
            throw new IllegalSyntaxException(variableTerm, "Invalid variable name");
        if (definedVariables.isVariableDefinedInSameScope(variableName))
            throw new IllegalSyntaxException(variableTerm, "Variable already defined");

        consumeCharactersFromTerm(termIterator, variableName.length());

        definedVariables.addDefinedVariable(variableName);

        if (isNextTermStartingWith(termIterator, ";"))
            return new DefineStatement(variableName);

        validateNextTermStartingWith(termIterator, "=");

        consumeCharactersFromTerm(termIterator, 1);

        final ExecutableStatement value = produceExpressionFromIterator(termIterator, definedVariables, true);
        return new DefineAndAssignStatement(variableName, value);
    }

    private ExecutableStatement produceReturnStatement(LastPeekingIterator<TermBlock> termIterator, DefinedVariables definedVariables) throws IllegalSyntaxException {
        consumeCharactersFromTerm(termIterator, 6);
        if (isNextTermStartingWith(termIterator, ";"))
            return new ReturnStatement(new ConstantStatement(new Variable(null)));
        return new ReturnStatement(produceExpressionFromIterator(termIterator, definedVariables, true));
    }

    private void consumeCharactersFromTerm(LastPeekingIterator<TermBlock> termIterator, int charCount) {
        final Term term = termIterator.peek().getTerm();
        String termText = term.getValue();
        int previousLength = termText.length();
        String termRemainder = termText.substring(charCount).trim();
        if (termRemainder.length() > 0)
            term.setValue(termRemainder, previousLength - termRemainder.length());
        else
            termIterator.next();
    }

    private void consumeSemicolon(LastPeekingIterator<TermBlock> termIterator) throws IllegalSyntaxException {
        Term term = peekNextProgramTermSafely(termIterator);
        String value = term.getValue();
        if (!value.startsWith(";"))
            throw new IllegalSyntaxException(term, "; expected");
        consumeCharactersFromTerm(termIterator, 1);
    }

    private ExecutableStatement produceExpressionFromIterator(LastPeekingIterator<TermBlock> termIterator, DefinedVariables definedVariables, boolean acceptsVariable) throws IllegalSyntaxException {
        if (isNextTermStartingWith(termIterator, "[") && acceptsVariable) {
            consumeCharactersFromTerm(termIterator, 1);
            return produceListDefinitionFromIterator(termIterator, definedVariables);
        }

        int line = getLine(termIterator);

        final ExecutableStatement executableStatement = parseExpression(line, termIterator, definedVariables, parseNextOperationToken(termIterator, definedVariables), Integer.MAX_VALUE);
        if (!acceptsVariable && executableStatement instanceof VariableStatement)
            throw new IllegalSyntaxException(termIterator, "Expression expected");
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

    private ExecutableStatement produceListDefinitionFromIterator(LastPeekingIterator<TermBlock> termIterator, DefinedVariables definedVariables) throws IllegalSyntaxException {
        final List<ExecutableStatement> values = parseParameters(termIterator, definedVariables, false, "]");
        return new ListDefineStatement(values);
    }

    private ExecutableStatement parseExpression(int line, LastPeekingIterator<TermBlock> termIterator, DefinedVariables definedVariables, ExecutableStatement left, int maxPriority) throws IllegalSyntaxException {
        // Based on algorithm from http://en.wikipedia.org/wiki/Operator-precedence_parser on March 28, 2013
        Operator operator;
        while ((operator = peekNextOperator(termIterator, left != null)) != null &&
                operator.getPriority() <= maxPriority) {
            if (operator.isBinary())
                left = produceBinaryExpression(line, termIterator, definedVariables, left, operator);
            else
                left = produceUnaryExpression(line, termIterator, definedVariables, left, operator);
        }

        return left;
    }

    private ExecutableStatement produceUnaryExpression(int line, LastPeekingIterator<TermBlock> termIterator, DefinedVariables definedVariables, ExecutableStatement left, Operator operator) throws IllegalSyntaxException {
        consumeCharactersFromTerm(termIterator, operator.getConsumeLength());
        List<ExecutableStatement> parameters = null;
        if (operator.isHasParameters())
            parameters = parseParameters(termIterator, definedVariables, operator.exactlyOneParameter(), operator.getParametersClosing());

        if (operator.isLeftAssociative())
            left = produceOperation(line, left, operator, null, parameters);
        else {
            ExecutableStatement operatorExpression;
            if (operator.isNamedOnRight()) {
                final Term term = peekNextProgramTermSafely(termIterator);
                String literal = getFirstLiteral(term);
                consumeCharactersFromTerm(termIterator, literal.length());
                operatorExpression = new NamedStatement(literal);
            } else
                operatorExpression = parseExpression(line, termIterator, definedVariables, parseNextOperationToken(termIterator, definedVariables), operator.getPriority());
            if (operator.isPre()) {
                if (operatorExpression == null)
                    throw new IllegalSyntaxException(termIterator, "Expression expected");
                left = produceOperation(line, operatorExpression, operator, null, parameters);
            } else {
                if (left == null)
                    throw new IllegalSyntaxException(termIterator, "Expression expected");
                left = produceOperation(line, null, operator, left, parameters);
            }
        }
        return left;
    }

    private ExecutableStatement produceBinaryExpression(int line, LastPeekingIterator<TermBlock> termIterator, DefinedVariables definedVariables, ExecutableStatement left, Operator operator) throws IllegalSyntaxException {
        final Term operatorTerm = termIterator.peek().getTerm();
        int operatorLine = operatorTerm.getLine();
        int operatorColumn = operatorTerm.getColumn();
        consumeCharactersFromTerm(termIterator, operator.getConsumeLength());

        List<ExecutableStatement> parameters = null;
        if (operator.isHasParameters())
            parameters = parseParameters(termIterator, definedVariables, operator.exactlyOneParameter(), operator.getParametersClosing());

        ExecutableStatement right;
        if (operator.isNamedOnRight()) {
            final Term term = peekNextProgramTermSafely(termIterator);
            String literal = getFirstLiteral(term);
            consumeCharactersFromTerm(termIterator, literal.length());
            right = new NamedStatement(literal);
        } else
            right = parseNextOperationToken(termIterator, definedVariables);
        if (right == null)
            throw new IllegalSyntaxException(termIterator, "Expression expected");
        right = produceExpressionOnRightSide(line, termIterator, definedVariables, left, operator, right);

        if (left == null)
            throw new IllegalSyntaxException(operatorLine, operatorColumn, "Expression expected");
        if (right == null)
            throw new IllegalSyntaxException(operatorLine, operatorColumn + operator.getConsumeLength(), "Expression expected");
        left = produceOperation(line, left, operator, right, parameters);
        return left;
    }

    private ExecutableStatement produceExpressionOnRightSide(int line, LastPeekingIterator<TermBlock> termIterator, DefinedVariables definedVariables, ExecutableStatement left, Operator operator, ExecutableStatement right) throws IllegalSyntaxException {
        Operator nextOperator;
        while ((nextOperator = peekNextOperator(termIterator, left != null)) != null &&
                (nextOperator.getPriority() < operator.getPriority() ||
                        (nextOperator.getPriority() == operator.getPriority() && !nextOperator.isLeftAssociative()))) {
            if (operator.isBinary())
                right = parseExpression(line, termIterator, definedVariables, right, nextOperator.getPriority());
            else {
                consumeCharactersFromTerm(termIterator, operator.getConsumeLength());
                if (operator.isPre()) {
                    if (right == null)
                        throw new IllegalSyntaxException(termIterator, "Expression expected");
                    right = produceOperation(line, null, nextOperator, right, parseParameters(termIterator, definedVariables, nextOperator.exactlyOneParameter(), nextOperator.getParametersClosing()));
                } else {
                    if (left == null)
                        throw new IllegalSyntaxException(termIterator, "Expression expected");
                    right = produceOperation(line, left, nextOperator, null, parseParameters(termIterator, definedVariables, nextOperator.exactlyOneParameter(), nextOperator.getParametersClosing()));
                }
            }
        }
        return right;
    }

    private List<ExecutableStatement> parseParameters(LastPeekingIterator<TermBlock> termIterator, DefinedVariables definedVariables, boolean exactlyOneParameter, String parametersClosing) throws IllegalSyntaxException {
        boolean first = true;
        List<ExecutableStatement> parameters;
        parameters = new ArrayList<ExecutableStatement>();
        while (!isNextTermStartingWith(termIterator, parametersClosing)) {
            if (!first) {
                if (exactlyOneParameter)
                    throw new IllegalSyntaxException(termIterator, parametersClosing + " expected");
                validateNextTermStartingWith(termIterator, ",");
                consumeCharactersFromTerm(termIterator, 1);
            }

            parameters.add(produceExpressionFromIterator(termIterator, definedVariables, true));
            first = false;
        }
        if (first && exactlyOneParameter)
            throw new IllegalSyntaxException(termIterator, parametersClosing + " expected");
        consumeCharactersFromTerm(termIterator, parametersClosing.length());
        return parameters;
    }

    private Operator peekNextOperator(LastPeekingIterator<TermBlock> termIterator, boolean hasLeft) throws IllegalSyntaxException {
        if (!termIterator.hasNext())
            return null;
        final Term term = peekNextProgramTermSafely(termIterator);
        String termValue = term.getValue();
        Operator operator = null;
        if (termValue.startsWith("=="))
            operator = Operator.EQUALS;
        else if (termValue.startsWith("!="))
            operator = Operator.NOT_EQUALS;
        else if (termValue.startsWith("="))
            operator = Operator.ASSIGNMENT;
        else if (termValue.startsWith("("))
            operator = Operator.FUNCTION_CALL;
        else if (termValue.startsWith("++"))
            if (hasLeft)
                operator = Operator.POST_INCREMENT;
            else
                operator = Operator.PRE_INCREMENT;
        else if (termValue.startsWith("--"))
            if (hasLeft)
                operator = Operator.POST_DECREMENT;
            else
                operator = Operator.PRE_DECREMENT;
        else if (termValue.startsWith("+="))
            operator = Operator.ADD_ASSIGN;
        else if (termValue.startsWith("-="))
            operator = Operator.SUBTRACT_ASSIGN;
        else if (termValue.startsWith("*="))
            operator = Operator.MULTIPLY_ASSIGN;
        else if (termValue.startsWith("/="))
            operator = Operator.DIVIDE_ASSIGN;
        else if (termValue.startsWith("%="))
            operator = Operator.MOD_ASSIGN;
        else if (termValue.startsWith("+"))
            operator = Operator.ADD;
        else if (termValue.startsWith("-")) {
            if (hasLeft)
                operator = Operator.SUBTRACT;
            else
                operator = Operator.NEGATIVE;
        } else if (termValue.startsWith("*"))
            operator = Operator.MULTIPLY;
        else if (termValue.startsWith("/"))
            operator = Operator.DIVIDE;
        else if (termValue.startsWith("%"))
            operator = Operator.MOD;
        else if (termValue.startsWith(">="))
            operator = Operator.GREATER_OR_EQUAL;
        else if (termValue.startsWith(">"))
            operator = Operator.GREATER;
        else if (termValue.startsWith("<="))
            operator = Operator.LESS_OR_EQUAL;
        else if (termValue.startsWith("<"))
            operator = Operator.LESS;
        else if (termValue.startsWith("."))
            operator = Operator.MEMBER_ACCESS;
        else if (termValue.startsWith("&&"))
            operator = Operator.AND;
        else if (termValue.startsWith("||"))
            operator = Operator.OR;
        else if (termValue.startsWith("!"))
            operator = Operator.NOT;
        else if (termValue.startsWith("["))
            operator = Operator.MAPPED_ACCESS;

        return operator;
    }

    private ExecutableStatement produceOperation(int line, ExecutableStatement left, Operator operator, ExecutableStatement right, List<ExecutableStatement> parameters) throws IllegalSyntaxException {
        if (operator == Operator.ASSIGNMENT)
            return new AssignStatement(left, right);
        else if (operator == Operator.FUNCTION_CALL)
            return new FunctionCallStatement(line, left, parameters);
        else if (operator == Operator.ADD)
            return new AddStatement(line, left, right, false);
        else if (operator == Operator.ADD_ASSIGN)
            return new AddStatement(line, left, right, true);
        else if (operator == Operator.EQUALS || operator == Operator.NOT_EQUALS)
            return new ComparisonStatement(left, operator, right);
        else if (operator == Operator.MEMBER_ACCESS) {
            return new MemberAccessStatement(line, left, ((NamedStatement) right).getName());
        } else if (operator == Operator.AND || operator == Operator.OR)
            return new LogicalOperatorStatement(line, left, operator, right);
        else if (operator == Operator.NOT)
            return new NegateStatement(line, left);
        else if (operator == Operator.NEGATIVE)
            return new NegativeStatement(line, left);
        else if (operator == Operator.MAPPED_ACCESS) {
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

    private ExecutableStatement parseNextOperationToken(LastPeekingIterator<TermBlock> termIterator, DefinedVariables definedVariables) throws IllegalSyntaxException {
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
                    result = produceExpressionFromIterator(termIterator, definedVariables, true);
                    validateNextTermStartingWith(termIterator, ")");
                    consumeCharactersFromTerm(termIterator, 1);
                } else if (Character.isDigit(termValue.charAt(0))) {
                    String numberInStr = getNumber(termValue);
                    consumeCharactersFromTerm(termIterator, numberInStr.length());
                    result = new ConstantStatement(new Variable(Float.parseFloat(numberInStr)));
                } else {
                    if (Character.isLetter(termValue.charAt(0))) {
                        String literal = getFirstLiteral(term);

                        consumeCharactersFromTerm(termIterator, literal.length());

                        if (literal.equals("true"))
                            result = new ConstantStatement(new Variable(true));
                        else if (literal.equals("false"))
                            result = new ConstantStatement(new Variable(false));
                        else if (literal.equals("null"))
                            result = new ConstantStatement(new Variable(null));
                        else if (literal.equals("function")) {
                            result = produceFunctionFromIterator(termIterator, definedVariables, term);
                        } else {
                            if (LangDefinition.isReservedWord(literal))
                                throw new IllegalSyntaxException(line, column, "Invalid variable name");
                            if (!definedVariables.isVariableDefined(literal))
                                throw new IllegalSyntaxException(line, column, "Variable " + literal + " not defined in scope");
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
            result = produceMapDefinitionFromBlock(termBlock, definedVariables);
            // Consume the block
            termIterator.next();
        }
        return result;
    }

    private ExecutableStatement produceFunctionFromIterator(LastPeekingIterator<TermBlock> termIterator, DefinedVariables definedVariables, Term term) throws IllegalSyntaxException {
        ExecutableStatement result;
        validateNextTermStartingWith(termIterator, "(");
        consumeCharactersFromTerm(termIterator, 1);

        List<String> parameterNames = new ArrayList<String>();
        while (!isNextTermStartingWith(termIterator, ")")) {
            String parameterName = getFirstLiteral(term);
            consumeCharactersFromTerm(termIterator, parameterName.length());
            parameterNames.add(parameterName);
            if (isNextTermStartingWith(termIterator, ","))
                consumeCharactersFromTerm(termIterator, 1);
        }
        consumeCharactersFromTerm(termIterator, 1);

        if (!termIterator.hasNext())
            throw new IllegalSyntaxException(termIterator, "{ expected");
        final TermBlock functionBodyBlock = termIterator.next();
        if (functionBodyBlock.isTerm())
            throw new IllegalSyntaxException(termIterator, "{ expected");

        definedVariables.pushNewContext();
        try {
            for (String parameterName : parameterNames)
                definedVariables.addDefinedVariable(parameterName);

            final List<ExecutableStatement> functionBody = seekStatementsInBlock(functionBodyBlock, definedVariables);
            result = new FunctionStatement(parameterNames, functionBody);
        } finally {
            definedVariables.popContext();
        }
        return result;
    }

    private ExecutableStatement produceMapDefinitionFromBlock(TermBlock termBlock, DefinedVariables definedVariables) throws IllegalSyntaxException {
        MapDefineStatement mapStatement = new MapDefineStatement();
        final LastPeekingIterator<TermBlock> iterator = new LastPeekingIterator<TermBlock>(Iterators.peekingIterator(termBlock.getTermBlocks().iterator()));
        boolean first = true;
        while (iterator.hasNext()) {
            if (!first) {
                validateNextTermStartingWith(iterator, ",");
                consumeCharactersFromTerm(iterator, 1);
            }
            final TermBlock property = iterator.peek();
            if (!property.isTerm())
                throw new IllegalSyntaxException(property.getBlockStartLine(), property.getBlockStartColumn(), "Property name expected");
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

            mapStatement.addProperty(propertyLine, propertyColumn, propertyName, produceExpressionFromIterator(iterator, definedVariables, true));

            first = false;
        }
        return mapStatement;
    }

    private String getNumber(String termValue) {
        StringBuilder result = new StringBuilder();
        boolean hasDot = false;
        final char[] chars = termValue.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (i == 0 && chars[i] == '-')
                result.append(chars[i]);
            else if (Character.isDigit(chars[i]))
                result.append(chars[i]);
            else if (chars[i] == '.' && !hasDot) {
                hasDot = true;
                result.append('.');
            } else
                return result.toString();
        }
        return result.toString();
    }

    private String getFirstLiteral(Term term) throws IllegalSyntaxException {
        StringBuilder sb = new StringBuilder();
        char[] chars = term.getValue().toCharArray();
        if (!Character.isLetter(chars[0]))
            throw new IllegalSyntaxException(term, "Expected expression");
        for (char c : chars) {
            if (Character.isLetterOrDigit(c))
                sb.append(c);
            else
                return sb.toString();
        }
        return sb.toString();
    }

    private Term peekNextProgramTermSafely(LastPeekingIterator<TermBlock> termIterator) throws IllegalSyntaxException {
        if (termIterator.hasNext()) {
            TermBlock termBlock = termIterator.peek();
            if (!termBlock.isTerm())
                throw new IllegalSyntaxException(termIterator, "Expression expected");

            Term term = termBlock.getTerm();
            if (term.getType() != Term.Type.PROGRAM)
                throw new IllegalSyntaxException(termIterator, "Expression expected");
            return term;
        } else
            throw new IllegalSyntaxException(termIterator, "Expression expected");
    }

    private TermBlock peekNextTermBlockSafely(LastPeekingIterator<TermBlock> termIterator) throws IllegalSyntaxException {
        if (termIterator.hasNext()) {
            return termIterator.peek();
        } else
            throw new IllegalSyntaxException(termIterator, "Expression expected");
    }

    private void validateNextTermStartingWith(LastPeekingIterator<TermBlock> termIterator, String text) throws IllegalSyntaxException {
        if (!isNextTermStartingWith(termIterator, text))
            throw new IllegalSyntaxException(termIterator, text + " expected");
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
            for (int i = 0; i < indent; i++)
                System.out.print("  ");
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
                        if (termBlocksStack.size() == 0)
                            throw new IllegalSyntaxException(term.getLine(), close, "Found closing bracket for no block");
                        currentBlock.terminateTermBlock(term.getLine(), close);
                        currentBlock = termBlocksStack.removeLast();
                        value = after;
                        columnIncrement += close + 1;
                    } else {
                        String valueTrimmed = value.trim();
                        int newColumn = columnIncrement + term.getColumn() + value.indexOf(valueTrimmed);
                        appendProgramTerm(currentBlock, valueTrimmed, term.getLine(), newColumn);
                        value = "";
                    }
                }
            } else if (term.getType() == Term.Type.STRING) {
                currentBlock.addTermBlock(term);
            }
        }

        if (termBlocksStack.size() > 0) {
            final Term lastTerm = terms.get(terms.size() - 1);
            throw new IllegalSyntaxException(lastTerm.getLine(), lastTerm.getColumn() + lastTerm.getValue().length(), "Unclosed bracket - }");
        }

        return result;
    }

    private void appendProgramTerm(TermBlock currentBlock, String text, int line, int column) {
        currentBlock.addTermBlock(new Term(Term.Type.PROGRAM, text, line, column));
    }

    private List<Term> parseToTerms(BufferedReader bufferedReader) throws IOException, IllegalSyntaxException {
        int lineNumber = 0;
        List<Term> terms = new ArrayList<Term>();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            termAndValidateLine(line, lineNumber, terms);
            lineNumber++;
        }
        return terms;
    }

    private void termAndValidateLine(String line, int lineNumber, List<Term> resultTerms) throws IllegalSyntaxException {
        // Remove all not needed white-space characters
        Term.Type type = Term.Type.PROGRAM;
        StringBuilder valueSoFar = new StringBuilder();
        char[] lineChars = line.toCharArray();
        int termStartColumn = 0;
        for (int i = 0; i < lineChars.length; i++) {
            if (type == Term.Type.PROGRAM) {
                if (lineChars[i] == '\"') {
                    if (valueSoFar.length() > 0)
                        resultTerms.add(new Term(type, valueSoFar.toString(), lineNumber, termStartColumn));
                    type = Term.Type.STRING;
                    valueSoFar = new StringBuilder();
                    termStartColumn = i + 1;
                } else if (lineChars[i] == '/' && i + 1 < lineChars.length && lineChars[i + 1] == '/') {
                    if (valueSoFar.length() > 0)
                        resultTerms.add(new Term(type, valueSoFar.toString(), lineNumber, termStartColumn));
                    type = Term.Type.COMMENT;
                    valueSoFar = new StringBuilder();
                    termStartColumn = i;
                } else {
                    valueSoFar.append(lineChars[i]);
                }
            } else if (type == Term.Type.STRING) {
                if (lineChars[i] == '\"') {
                    resultTerms.add(new Term(type, valueSoFar.toString(), lineNumber, termStartColumn));
                    type = Term.Type.PROGRAM;
                    valueSoFar = new StringBuilder();
                    termStartColumn = i + 1;
                } else if (lineChars[i] == '\\') {
                    i++;
                    if (i < lineChars.length) {
                        if (lineChars[i] == '\"' || lineChars[i] == '\\')
                            valueSoFar.append(lineChars[i]);
                        else
                            throw new IllegalSyntaxException(lineNumber, i, "Illegal escape sequence in String \\" + lineChars[i]);
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

        if (valueSoFar.length() > 0)
            resultTerms.add(new Term(type, (type == Term.Type.PROGRAM) ? valueSoFar.toString() : valueSoFar.toString(), lineNumber, termStartColumn));
    }
}
