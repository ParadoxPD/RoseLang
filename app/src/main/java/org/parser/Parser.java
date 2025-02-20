package org.parser;

import org.lexer.Token;
import org.lexer.TokenList;
import org.error.ErrorList;
import org.error.ParserError;

import java.util.Vector;

public class Parser {

	private Vector<Token> tokens;
	private Vector<ParserError> errors;
	private Token curr = null;
	private Token peek = null;
	private int currPos;

	public Parser(Vector<Token> tokens) {
		this.tokens = tokens;
		this.currPos = 0;
		this.errors = new Vector<ParserError>();

		// TODO: add error checking for the existence of tokens before accessing it.
		//
		this.nextToken();
		this.nextToken();
	}

	void nextToken() {
		if (!this.tokens.get(this.currPos).getType().getType().equals(TokenList.EOF)) {
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
		return this.curr.getType().getType() == type;
	}

	boolean peekTokenIs(String type) {
		return this.peek.getType().getType() == type;
	}

	boolean expectPeek(String type) {
		if (this.peekTokenIs(type)) {
			this.nextToken();
			return true;
		} else {
			this.errors.addElement(new ParserError(ErrorList.INVALID_SYNTAX,
					"Expected " + this.peek.getType().getType() + "Got : " + type));
			return false;
		}
	}

	Statement parseStatement() {
		switch (this.curr.getType().getType()) {
			case TokenList.LET:
				return this.parseLetStatement();
			case TokenList.RETURN:
				return this.parseReturnStatement();
			default:
				return null;
		}
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
