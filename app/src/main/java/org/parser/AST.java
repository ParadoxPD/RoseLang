package org.parser;

import org.lexer.Token;

import java.util.Vector;

interface Node {
	String getTokenValue();

}

interface Statement extends Node {
	void statementNode();
}

interface Expression extends Node {

	void expresionNode();
}

class Identifier implements Expression {
	Token token;
	String value;

	Identifier(Token tok, String val) {
		this.token = tok;
		this.value = val;
	}

	@Override
	public String getTokenValue() {
		return this.token.getTokenValue();
	}

	@Override
	public void expresionNode() {
		// TODO Auto-generated method stub

	}

}

class LetStatement implements Statement {
	Token token;
	Identifier name;
	Expression value;

	LetStatement(Token tok) {
		this.token = tok;
		name = null;
		value = null;
	}

	@Override
	public void statementNode() {

	}

	@Override
	public String getTokenValue() {
		return this.token.getTokenValue();
	}

	public Identifier getName() {
		return this.name;
	}

	public Expression getValue() {
		return this.value;
	}

	public void setName(Identifier name) {
		this.name = name;
	}

	public void setValue(Expression value) {
		this.value = value;
	}

}

class ReturnStatement implements Statement {

	Token token;
	Expression returnValue;

	public ReturnStatement(Token tok) {
		this.token = tok;
		this.returnValue = null;
	}

	@Override
	public void statementNode() {
		// TODO Auto-generated method stub

	}

	@Override
	public String getTokenValue() {
		return this.token.getTokenValue();
	}

	public Expression getReturnValue() {
		return this.returnValue;
	}

	public void setReturnValue(Expression returnValue) {
		this.returnValue = returnValue;
	}

}

class Program implements Node {

	// TODO: Refactor the statements to have some kind of getter and setter
	Vector<Statement> statements;

	Program() {
		this.statements = new Vector<Statement>();
	}

	@Override
	public String getTokenValue() {
		if (this.statements.isEmpty()) {
			return "";
		}
		return this.statements.get(0).getTokenValue();
	}

}

public class AST {

}
