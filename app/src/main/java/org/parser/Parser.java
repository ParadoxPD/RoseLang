package org.parser;

import org.debugger.Debugger;
import org.error.*;
import org.lexer.*;
import org.parser.expressions.*;
import org.parser.literals.*;
import org.parser.statements.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

abstract class PrefixParser {

    abstract Expression parse();
}

abstract class InfixParser {
    abstract Expression parse(Expression left);
}

// NOTE: Implement ternary operation

public class Parser {

    private Vector<Token> tokens;
    private Vector<ParserError> errors;
    private Token curr = null;
    private Token peek = null;
    private boolean peekAvailable;
    private int currPos;
    private Map<String, PrefixParser> prefixParsers;
    private Map<String, InfixParser> infixParsers;
    private Program program;
    private Debugger debugger;

    public Parser(Vector<Token> tokens, Debugger debugger) {
        this.tokens = tokens;
        this.currPos = -2;
        this.errors = new Vector<ParserError>();
        this.peekAvailable = true;
        this.program = null;
        this.debugger = debugger;

        this.nextToken();
        this.nextToken();

        this.prefixParsers = new HashMap<String, PrefixParser>();
        this.infixParsers = new HashMap<String, InfixParser>();
        this.registerAllParsers();
    }

    void registerAllParsers() {

        PrefixParser integerParser =
                new PrefixParser() {
                    @Override
                    public Expression parse() {
                        IntegerLiteral lit = new IntegerLiteral(curr);

                        // TODO: ADD ERROR CHECKING FOR INTEGER VALUE
                        int val = Integer.parseInt(curr.getTokenValue());
                        lit.setValue(val);
                        return lit;
                    }
                };
        PrefixParser floatParser =
                new PrefixParser() {
                    @Override
                    public Expression parse() {
                        FloatLiteral lit = new FloatLiteral(curr);

                        // TODO: ADD ERROR CHECKING FOR FLOAT VALUE
                        float val = Float.parseFloat(curr.getTokenValue());
                        lit.setValue(val);
                        return lit;
                    }
                };
        PrefixParser idenParser =
                new PrefixParser() {
                    @Override
                    public Expression parse() {
                        return new Identifier(curr, curr.getTokenValue());
                    }
                };

        PrefixParser prefixExpressionParser =
                new PrefixParser() {
                    @Override
                    Expression parse() {
                        PrefixExpression exp = new PrefixExpression(curr, curr.getTokenValue());
                        nextToken();
                        exp.setRight(parseExpression(PrecedenceList.PREFIX));
                        return exp;
                    }
                };

        PrefixParser booleanParser =
                new PrefixParser() {
                    @Override
                    Expression parse() {
                        return new BooleanLiteral(curr, currTokenIs(TokenList.TRUE));
                    }
                };

        PrefixParser groupedExpressionParser =
                new PrefixParser() {
                    @Override
                    Expression parse() {
                        nextToken();
                        Expression exp = parseExpression(PrecedenceList.LOWEST);

                        if (!expectPeek(TokenList.PAREN_CLOSE)) {
                            errors.addElement(new ParserError("Missing Symbol", "( missing"));
                            return null;
                        }
                        return exp;
                    }
                };

        PrefixParser ifExpressionParser =
                new PrefixParser() {
                    @Override
                    Expression parse() {
                        debugger.log("INSIDE IF BLOCK");
                        IfExpression stm = new IfExpression(curr);
                        if (!expectPeek(TokenList.PAREN_OPEN)) {
                            errors.addElement(new ParserError("Missing Symbol", "( missing"));
                            return null;
                        }

                        nextToken();
                        stm.setCondition(parseExpression(PrecedenceList.LOWEST));

                        if (!expectPeek(TokenList.PAREN_CLOSE)) {
                            errors.addElement(new ParserError("Missing Symbol", ") missing"));
                            return null;
                        }
                        if (!expectPeek(TokenList.BRACE_OPEN)) {
                            errors.addElement(new ParserError("Missing Symbol", "{ missing"));
                            return null;
                        }
                        stm.setConsequence(parseBlockStatement());

                        while (peekTokenIs(TokenList.ELIF)) {

                            nextToken();
                            ElifExpression exp = new ElifExpression(curr);
                            if (!expectPeek(TokenList.PAREN_OPEN)) {
                                errors.addElement(new ParserError("Missing Symbol", "( missing"));
                                return null;
                            }

                            nextToken();
                            exp.setCondition(parseExpression(PrecedenceList.LOWEST));
                            if (!expectPeek(TokenList.PAREN_CLOSE)) {
                                errors.addElement(new ParserError("Missing Symbol", ") missing"));
                                return null;
                            }
                            if (!expectPeek(TokenList.BRACE_OPEN)) {
                                errors.addElement(new ParserError("Missing Symbol", "{ missing"));
                                return null;
                            }
                            exp.setConsequence(parseBlockStatement());
                            stm.addElifExpression(exp);
                            debugger.log(exp.print("ELIF STATEMENT : "));
                        }

                        if (peekTokenIs(TokenList.ELSE)) {
                            nextToken();
                            if (!expectPeek(TokenList.BRACE_OPEN)) {
                                errors.addElement(new ParserError("Missing Symbol", "{ missing"));
                                return null;
                            }
                            stm.setAlternative(parseBlockStatement());
                        }
                        debugger.log(stm.print("IF STATEMENT : "));

                        return stm;
                    }
                };
        // PrefixParser functionParser =
        //        new PrefixParser() {
        //            @Override
        //            Expression parse() {
        //                FunctionLiteral fnt = new FunctionLiteral(curr);

        //                if (!expectPeek(TokenList.IDENTIFIER)) {
        //                    errors.addElement(
        //                            new ParserError("Identifier missing", "Function needs a
        // name"));
        //                    return null;
        //                }
        //                fnt.setName(new Identifier(curr, curr.getTokenValue()));

        //                if (!expectPeek(TokenList.PAREN_OPEN)) {
        //                    errors.addElement(new ParserError("Missing Symbol", "( missing"));
        //                    return null;
        //                }
        //                fnt.addParameters(parseFunctionParameters());

        //                if (!expectPeek(TokenList.BRACE_OPEN)) {
        //                    errors.addElement(new ParserError("Missing Symbol", "{ missing"));
        //                    return null;
        //                }
        //                fnt.addBody(parseBlockStatement());
        //                debugger.log(fnt.print("Function : "));
        //                return fnt;
        //            }
        //        };

        PrefixParser stringParser =
                new PrefixParser() {
                    @Override
                    Expression parse() {
                        return new StringLiteral(curr, curr.getTokenValue());
                    }
                };

        PrefixParser arrayParser =
                new PrefixParser() {
                    @Override
                    Expression parse() {
                        return new ArrayLiteral(
                                curr, parseExpressionList(TokenList.SQUARE_BRACKET_CLOSE));
                    }
                };

        PrefixParser hashLiteralParser =
                new PrefixParser() {
                    @Override
                    Expression parse() {
                        HashLiteral hash =
                                new HashLiteral(curr, new HashMap<Expression, Expression>());

                        while (!peekTokenIs(TokenList.BRACE_CLOSE)) {
                            nextToken();
                            Expression key = parseExpression(PrecedenceList.LOWEST);

                            if (!expectPeek(TokenList.COLON)) {
                                errors.addElement(new ParserError("Missing Symbol", ": missing"));
                                return null;
                            }
                            nextToken();
                            Expression value = parseExpression(PrecedenceList.LOWEST);

                            hash.addElement(key, value);

                            if (!peekTokenIs(TokenList.BRACE_CLOSE)
                                    && !expectPeek(TokenList.COMMA)) {
                                errors.addElement(new ParserError("Missing Symbol", "}, missing"));
                                return null;
                            }
                        }
                        if (!expectPeek(TokenList.BRACE_CLOSE)) {
                            errors.addElement(new ParserError("Missing Symbol", "} missing"));
                            return null;
                        }

                        return hash;
                    }
                };

        this.registerPrefixParser((TokenList.IDENTIFIER), idenParser);
        this.registerPrefixParser((TokenList.INT), integerParser);
        this.registerPrefixParser((TokenList.FLOAT), floatParser);
        this.registerPrefixParser((TokenList.BANG), prefixExpressionParser);
        this.registerPrefixParser((TokenList.MINUS), prefixExpressionParser);
        this.registerPrefixParser((TokenList.TRUE), booleanParser);
        this.registerPrefixParser((TokenList.FALSE), booleanParser);
        this.registerPrefixParser((TokenList.PAREN_OPEN), groupedExpressionParser);
        this.registerPrefixParser((TokenList.BRACE_OPEN), hashLiteralParser);
        this.registerPrefixParser((TokenList.SQUARE_BRACKET_OPEN), arrayParser);
        this.registerPrefixParser((TokenList.IF), ifExpressionParser);
        // this.registerPrefixParser((TokenList.FUNCTION), functionParser);
        this.registerPrefixParser((TokenList.STRING), stringParser);

        InfixParser infixParser =
                new InfixParser() {
                    @Override
                    Expression parse(Expression left) {
                        InfixExpression exp = new InfixExpression(curr, curr.getTokenValue(), left);

                        int precedence = currPrecedence();
                        nextToken();
                        exp.setRight(parseExpression(precedence));

                        return exp;
                    }
                };

        InfixParser dotParser =
                new InfixParser() {
                    @Override
                    Expression parse(Expression left) {
                        DotExpression exp = new DotExpression(curr, left);

                        int precedence = currPrecedence();
                        nextToken();
                        Expression right = parseExpression(precedence);
                        System.out.println(precedence);
                        right.print("Right Dot:");
                        exp.setRight(right);

                        return exp;
                    }
                };

        InfixParser callExpressionParser =
                new InfixParser() {
                    @Override
                    Expression parse(Expression function) {

                        debugger.log("FUNCTION : " + function);
                        CallExpression exp;
                        if (function == null) {
                            exp =
                                    new CallExpression(
                                            curr,
                                            new Identifier(new Token("test", "test"), "test"));
                        } else {
                            exp = new CallExpression(curr, function);
                        }
                        exp.addArguments(parseExpressionList(TokenList.PAREN_CLOSE));
                        debugger.log(exp.print("Call Exp: "));
                        return exp;
                    }
                };

        InfixParser indexExpressionParser =
                new InfixParser() {
                    @Override
                    Expression parse(Expression left) {
                        IndexExpression exp = new IndexExpression(curr, left);

                        nextToken();
                        exp.setIndex(parseExpression(PrecedenceList.LOWEST));

                        if (!expectPeek(TokenList.SQUARE_BRACKET_CLOSE)) {
                            errors.addElement(new ParserError("Missing Symbol", "] missing"));
                            return null;
                        }
                        return exp;
                    }
                };

        this.registerInfixParser((TokenList.PLUS), infixParser);
        this.registerInfixParser((TokenList.MINUS), infixParser);
        this.registerInfixParser((TokenList.SLASH), infixParser);
        this.registerInfixParser((TokenList.ASTERISK), infixParser);
        this.registerInfixParser((TokenList.EQ), infixParser);
        this.registerInfixParser((TokenList.NOT_EQ), infixParser);
        this.registerInfixParser((TokenList.LT), infixParser);
        this.registerInfixParser((TokenList.GT), infixParser);
        this.registerInfixParser((TokenList.GTE), infixParser);
        this.registerInfixParser((TokenList.LTE), infixParser);
        this.registerInfixParser((TokenList.CHARAT), infixParser);
        this.registerInfixParser((TokenList.DOT), dotParser);
        this.registerInfixParser((TokenList.PAREN_OPEN), callExpressionParser);
        this.registerInfixParser((TokenList.SQUARE_BRACKET_OPEN), indexExpressionParser);
    }

    void registerPrefixParser(String type, PrefixParser parser) {
        this.prefixParsers.put(type, parser);
    }

    void registerInfixParser(String type, InfixParser parser) {
        this.infixParsers.put(type, parser);
    }

    int peekPrecedence() {
        if (this.peekAvailable) {
            if (PrecedenceList.Precedences.containsKey(this.peek.getType())) {

                return PrecedenceList.Precedences.get(this.peek.getType());
            } else {
                return PrecedenceList.LOWEST;
            }
        } else {
            return PrecedenceList.LOWEST;
        }
    }

    int currPrecedence() {
        if (PrecedenceList.Precedences.containsKey(this.curr.getType())) {

            return PrecedenceList.Precedences.get(this.curr.getType());
        } else {
            return PrecedenceList.LOWEST;
        }
    }

    void nextToken() {
        this.currPos++;
        this.curr = this.peek;
        if (!this.currTokenIs(TokenList.EOF)) {
            this.peek = this.tokens.get(this.currPos + 1);
        } else {
            this.peek = null;
            this.peekAvailable = false;
            this.debugger.log("Out of Tokens. If its not EOF then you might have fked up.");
            // System.exit(1);
        }
    }

    boolean currTokenIs(String type) {
        return this.curr != null && this.curr.getType().equals(type);
    }

    boolean peekTokenIs(String type) {
        if (!this.peekAvailable) {
            return false;
        }
        return this.peek.getType().equals(type);
    }

    boolean expectPeek(String type) {
        if (this.peekTokenIs(type)) {
            this.nextToken();
            return true;
        } else {
            this.errors.addElement(
                    new ParserError(
                            ErrorList.INVALID_SYNTAX,
                            "Expected " + this.peek.getType() + " Got : " + type));
            return false;
        }
    }

    Statement parseStatement() {
        // NOTE: Properly implement the '=' parsing (It has some off by one bug)
        switch (this.curr.getType()) {
            case TokenList.LET:
                return this.parseLetStatement();
            case TokenList.RETURN:
                return this.parseReturnStatement();
            case TokenList.WHILE:
                return this.parseWhileStatement();
            case TokenList.FUNCTION:
                return this.parseFunction();

            case TokenList.IDENTIFIER:
                return this.parseIdentifierStatement();
            default:
                return this.parseExpressionStatement();
        }
    }

    Statement parseFunction() {
        FunctionLiteral fnt = new FunctionLiteral(curr);

        if (!expectPeek(TokenList.IDENTIFIER)) {
            errors.addElement(new ParserError("Identifier missing", "Function needs a name"));
            return null;
        }
        fnt.setName(new Identifier(curr, curr.getTokenValue()));

        if (!expectPeek(TokenList.PAREN_OPEN)) {
            errors.addElement(new ParserError("Missing Symbol", "( missing"));
            return null;
        }
        fnt.addParameters(parseFunctionParameters());

        if (!expectPeek(TokenList.BRACE_OPEN)) {
            errors.addElement(new ParserError("Missing Symbol", "{ missing"));
            return null;
        }
        fnt.addBody(parseBlockStatement());
        debugger.log(fnt.print("Function : "));
        return fnt;
    }

    Statement parseIdentifierStatement() {
        // NOTE: MAYBE BUGGY?????
        if (this.peekTokenIs(TokenList.ASSIGN)) {
            return this.parseAssignmentStatement();
        }
        return this.parseExpressionStatement();
    }

    Statement parseAssignmentStatement() {

        Identifier name = new Identifier(this.curr, this.curr.getTokenValue());
        this.nextToken();
        AssignmentStatement stm = new AssignmentStatement(this.curr, name);
        this.nextToken();
        Expression exp = this.parseExpression(PrecedenceList.LOWEST);
        stm.setExpression(exp);
        if (!this.peekTokenIs(TokenList.SEMICOLON)) {
            errors.addElement(new ParserError("Missing Symbol", "; Missing"));
            return null;
        }

        this.nextToken();
        this.debugger.log(name.print("Identifier Name :"));
        this.debugger.log(exp.print("Assigned Expression : "));
        this.debugger.log(stm.print("Assignment Statement : "));
        return stm;
    }

    Statement parseWhileStatement() {
        WhileStatement stm = new WhileStatement(this.curr);
        if (!this.expectPeek(TokenList.PAREN_OPEN)) {
            errors.addElement(new ParserError("Missing Symbol", "( missing"));
            return null;
        }

        this.nextToken();
        stm.setCondition(parseExpression(PrecedenceList.LOWEST));

        if (!this.expectPeek(TokenList.PAREN_CLOSE)) {
            errors.addElement(new ParserError("Missing Symbol", ") missing"));
            return null;
        }
        if (!this.expectPeek(TokenList.BRACE_OPEN)) {
            errors.addElement(new ParserError("Missing Symbol", "{ missing"));
            return null;
        }
        stm.setBody(parseBlockStatement());
        this.debugger.log(stm.print("While Loop : "));
        return stm;
    }

    ExpressionStatement parseExpressionStatement() {
        ExpressionStatement smt =
                new ExpressionStatement(this.curr, this.parseExpression(PrecedenceList.LOWEST));

        if (this.peekTokenIs(TokenList.SEMICOLON)) this.nextToken();

        this.debugger.log(smt.print("Exp Stm: "));
        return smt;
    }

    Expression parseExpression(int precedence) {

        if (!this.prefixParsers.containsKey(this.curr.getType())) {
            this.errors.addElement(
                    new ParserError("", "No Prefix Parser found for : " + this.curr.getType()));
            return null;
        }

        PrefixParser prefix = this.prefixParsers.get(this.curr.getType());
        Expression leftExp = prefix.parse();
        if (leftExp != null) this.debugger.log(leftExp.print("Left Exp: "));

        while (!this.peekTokenIs(TokenList.SEMICOLON) && precedence < this.peekPrecedence()) {
            debugger.log("Are we here even if we should not be here?");
            if (this.infixParsers.containsKey(this.peek.getType())) {

                debugger.log("Are we here even if we should not be here?");
                InfixParser infix = this.infixParsers.get(this.peek.getType());
                this.nextToken();
                leftExp = infix.parse(leftExp);
            } else {
                return leftExp;
            }
        }
        return leftExp;
    }

    Vector<Expression> parseExpressionList(String end) {
        Vector<Expression> elements = new Vector<Expression>();

        if (this.peekTokenIs(end)) {
            this.nextToken();
            return elements;
        }

        this.nextToken();
        elements.addElement(this.parseExpression(PrecedenceList.LOWEST));

        while (this.peekTokenIs(TokenList.COMMA)) {
            this.nextToken();
            this.nextToken();
            elements.addElement(this.parseExpression(PrecedenceList.LOWEST));
        }

        if (!this.expectPeek(end)) {
            errors.addElement(new ParserError("Missing Symbol", "Expression not complete"));
            return null;
        }
        return elements;
    }

    LetStatement parseLetStatement() {
        // FIX: Let Statements with function signature should not have a secondary
        // identiifer

        LetStatement stm = new LetStatement(this.curr);

        if (!this.expectPeek(TokenList.IDENTIFIER)) {
            errors.addElement(new ParserError("Missing Symbol", "Identifier missing"));
            return null;
        }

        stm.setName(new Identifier(this.curr, this.curr.getTokenValue()));

        if (!this.expectPeek(TokenList.ASSIGN)) {

            errors.addElement(new ParserError("Missing Symbol", "= missing"));
            return null;
        }

        this.nextToken();

        stm.setValue(this.parseExpression(PrecedenceList.LOWEST));

        if (this.peekTokenIs(TokenList.SEMICOLON)) {
            this.nextToken();
        }

        this.debugger.log(stm.print("Let Statement : "));
        return stm;
    }

    ReturnStatement parseReturnStatement() {
        ReturnStatement stm = new ReturnStatement(this.curr);

        this.nextToken();

        stm.setReturnValue(this.parseExpression(PrecedenceList.LOWEST));

        if (!this.peekTokenIs(TokenList.SEMICOLON)) {
            errors.addElement(new ParserError("Missing Symbol", "; Missing"));
            return null;
        }

        this.nextToken();

        this.debugger.log(stm.print("Return Statement : "));
        return stm;
    }

    BlockStatement parseBlockStatement() {
        BlockStatement block = new BlockStatement(this.curr);
        this.nextToken();

        while (!(this.currTokenIs(TokenList.BRACE_CLOSE) || this.currTokenIs(TokenList.EOF))) {
            debugger.log("PArsing Bloack expression");
            Statement stm = this.parseStatement();
            if (stm != null) {
                block.addStatement(stm);
            }
            this.nextToken();
        }
        return block;
    }

    Vector<Identifier> parseFunctionParameters() {
        Vector<Identifier> parameters = new Vector<Identifier>();
        if (this.peekTokenIs(TokenList.PAREN_CLOSE)) {
            this.nextToken();
            return parameters;
        }
        this.nextToken();

        Identifier ident = new Identifier(this.curr, this.curr.getTokenValue());
        parameters.addElement(ident);

        while (this.peekTokenIs(TokenList.COMMA)) {
            this.nextToken();
            this.nextToken();
            ident = new Identifier(this.curr, this.curr.getTokenValue());
            parameters.addElement(ident);
        }

        if (!this.expectPeek(TokenList.PAREN_CLOSE)) {
            errors.addElement(new ParserError("Missing Symbol", ") missing"));
            return null;
        }
        return parameters;
    }

    public Program parseProgram() {

        this.program = new Program();
        // this.tokens.getLast().printToken();

        while (this.peek != null) {
            // this.curr.printToken();
            Statement stm = this.parseStatement();
            if (stm != null) {
                program.statements.addElement(stm);
            }
            this.nextToken();
        }

        if (this.program.size() > 0) return this.program;
        else this.errors.add(new ParserError("", "Unable to parse program"));
        return null;
    }

    public void printProgram() {
        this.debugger.log(this.program.print("Parsed Program : "));
    }

    public Vector<ParserError> getErrors() {
        return this.errors;
    }

    public static void main(String[] args) {
        System.out.println("Parsing :");
    }
}
