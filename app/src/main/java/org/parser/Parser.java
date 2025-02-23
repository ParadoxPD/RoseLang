package org.parser;

import org.lexer.Token;
import org.lexer.TokenList;
import org.error.ErrorList;
import org.error.ParserError;

import java.util.Vector;
import java.util.Map;
import java.util.HashMap;

abstract class PrefixParser {

	abstract Expression parse();
}

abstract class InfixParser {

	// private Expression left;

	InfixParser() {
		// this.left = null;
	}

	// InfixParser(Expression left) {
	// this.left = left;
	// }

	// abstract Expression parse();

	abstract Expression parse(Expression left);

	// public Expression getLeft() {
	// return this.left;
	// }
}

public class Parser {

	private Vector<Token> tokens;
	private Vector<ParserError> errors;
	private Token curr = null;
	private Token peek = null;
	private int currPos;
	private Map<String, PrefixParser> prefixParsers;
	private Map<String, InfixParser> infixParsers;

	public Parser(Vector<Token> tokens) {
		this.tokens = tokens;
		this.currPos = 0;
		this.errors = new Vector<ParserError>();

		this.nextToken();
		this.nextToken();

		this.prefixParsers = new HashMap<String, PrefixParser>();
		this.infixParsers = new HashMap<String, InfixParser>();
		this.registerAllParsers();

	}

	void registerAllParsers() {
		PrefixParser integerParser = new PrefixParser() {
			@Override
			public Expression parse() {
				IntegerLiteral lit = new IntegerLiteral(curr);

				// TODO: ADD ERROR CHECKING FOR INTEGER VALUE
				int val = Integer.parseInt(curr.getTokenValue());
				lit.setValue(val);
				return lit;

			}
		};
		PrefixParser idenParser = new PrefixParser() {
			@Override
			public Expression parse() {
				return new Identifier(curr, curr.getTokenValue());
			}
		};

		PrefixParser prefixExpressionParser = new PrefixParser() {
			@Override
			Expression parse() {
				PrefixExpression exp = new PrefixExpression(curr, curr.getTokenValue());
				nextToken();
				exp.setRight(parseExpression(PrecedenceList.PREFIX));
				return exp;
			}
		};

		this.registerPrefixParser((TokenList.IDENTIFIER), idenParser);
		this.registerPrefixParser((TokenList.INT), integerParser);
		this.registerPrefixParser((TokenList.BANG), prefixExpressionParser);
		this.registerPrefixParser((TokenList.MINUS), prefixExpressionParser);

		InfixParser infixParser = new InfixParser() {
			@Override
			Expression parse(Expression left) {
				InfixExpression exp = new InfixExpression(curr, curr.getTokenValue(), left);

				int precedence = currPrecedence();
				nextToken();
				exp.setRight(parseExpression(precedence));

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

	}

	void registerPrefixParser(String type, PrefixParser parser) {
		this.prefixParsers.put(type, parser);
	}

	void registerInfixParser(String type, InfixParser parser) {
		this.infixParsers.put(type, parser);
	}

	int peekPrecedence() {
		if (PrecedenceList.Precedences.containsKey(this.peek.getType())) {

			return PrecedenceList.Precedences.get(this.peek.getType());
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
		if (!this.tokens.get(this.currPos).getType().equals(TokenList.EOF)) {
			this.curr = this.peek;
			this.peek = this.tokens.get(this.currPos++);
		} else {
			this.curr = this.peek;
			this.peek = null;
			errors.addElement(new ParserError(null, "Out of Tokens. Weird???"));
			// System.exit(1);
		}
	}

	boolean currTokenIs(String type) {
		return this.curr.getType() == type;
	}

	boolean peekTokenIs(String type) {
		return this.peek.getType() == type;
	}

	boolean expectPeek(String type) {
		if (this.peekTokenIs(type)) {
			this.nextToken();
			return true;
		} else {
			this.errors.addElement(new ParserError(ErrorList.INVALID_SYNTAX,
					"Expected " + this.peek.getType() + "Got : " + type));
			return false;
		}
	}

	Statement parseStatement() {
		switch (this.curr.getType()) {
			case TokenList.LET:
				return this.parseLetStatement();
			case TokenList.RETURN:
				return this.parseReturnStatement();
			default:
				return this.parseExpressionStatement();
		}
	}

	ExpressionStatement parseExpressionStatement() {
		ExpressionStatement smt = new ExpressionStatement(this.curr,
				this.parseExpression(PrecedenceList.LOWEST));

		if (this.peekTokenIs(TokenList.SEMICOLON)) {
			this.nextToken();
		}
		smt.print("Exp : ");

		return smt;

	}

	Expression parseExpression(int precedence) {

		if (!this.prefixParsers.containsKey(this.curr.getType())) {
			this.errors.addElement(
					new ParserError("", "No Prefix Parser found for : "
							+ this.curr.getType()));
			return null;
		}

		PrefixParser prefix = this.prefixParsers.get(this.curr.getType());
		Expression leftExp = prefix.parse();
		// leftExp.print("Left Exp: ");

		while (!this.peekTokenIs(TokenList.SEMICOLON) && precedence < this.peekPrecedence()) {

			if (this.infixParsers.containsKey(this.peek.getType())) {

				InfixParser infix = this.infixParsers.get(this.peek.getType());
				this.nextToken();
				leftExp = infix.parse(leftExp);
			} else {
				return leftExp;
			}
		}
		return leftExp;
	}

	LetStatement parseLetStatement() {
		LetStatement stm = new LetStatement(this.curr);

		if (!this.expectPeek(TokenList.IDENTIFIER)) {
			return null;
		}

		stm.setName(new Identifier(this.curr, this.curr.getTokenValue()));

		if (!this.expectPeek(TokenList.ASSIGN)) {
			return null;
		}

		while (!this.currTokenIs(TokenList.SEMICOLON)) {
			this.nextToken();
		}
		return stm;
	}

	ReturnStatement parseReturnStatement() {
		ReturnStatement stm = new ReturnStatement(this.curr);

		this.nextToken();

		while (!this.currTokenIs(TokenList.SEMICOLON)) {
			this.nextToken();
		}

		return stm;
	}

	public Program parseProgram() {

		Program program = new Program();
		// this.tokens.getLast().printToken();

		while (this.peek != null) {
			this.curr.printToken();
			Statement stm = this.parseStatement();
			if (stm != null) {
				program.statements.addElement(stm);
			}
			this.nextToken();
		}

		return program;

	}

	public Vector<ParserError> getErrors() {
		return this.errors;
	}

	public static void main(String[] args) {
		System.out.println("Parsing :");

	}
}
