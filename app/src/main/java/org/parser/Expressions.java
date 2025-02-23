package org.parser;

import org.lexer.Token;

interface Expression extends Node {

	void expresionNode();
}

class PrefixExpression implements Expression {
	private Token token;
	private String operator;
	private Expression right;

	public PrefixExpression(Token tok, String op) {
		this.token = tok;
		this.operator = op;
	}

	@Override
	public void expresionNode() {
	}

	@Override
	public String getTokenValue() {
		return this.token.getTokenValue();
	}

	@Override
	public String getNodeValue() {
		return "(" + this.operator + this.right.getNodeValue() + ")";

	}

	@Override
	public void print(String msg) {
		System.out.println(msg + this.getNodeValue());
	}

	public void setRight(Expression right) {
		this.right = right;
	}
}

class InfixExpression implements Expression {
	Token token;
	Expression left;
	String operator;
	Expression right;

	public InfixExpression(Token tok, String op, Expression left) {
		this.token = tok;
		this.operator = op;
		this.left = left;
	}

	@Override
	public void expresionNode() {

	}

	@Override
	public String getTokenValue() {
		return this.token.getTokenValue();
	}

	@Override
	public String getNodeValue() {
		return "(" + this.left.getNodeValue() + " " + this.operator + " " + this.right.getNodeValue() + ")";
	}

	@Override
	public void print(String msg) {
		System.out.println(msg + this.getNodeValue());
	}

	public void setRight(Expression exp) {
		this.right = exp;
	}

	public Expression getLeft() {
		return this.left;
	}
}
