package org.parser;

import org.lexer.Token;

class IntegerLiteral implements Expression {
	Token token;
	int value;

	IntegerLiteral(Token tok) {
		this.token = tok;

	}

	IntegerLiteral(Token tok, int val) {
		this.token = tok;
		this.value = val;
	}

	@Override
	public void expresionNode() {
		// TODO Auto-generated method stub

	}

	@Override
	public String getTokenValue() {
		return this.token.getTokenValue();
	}

	@Override
	public String getNodeValue() {
		return this.token.getTokenValue();
	}

	public void setValue(int val) {
		this.value = val;
	}

	@Override
	public void print(String msg) {
		System.out.println(msg + this.getNodeValue());
	}

}

class BooleanLiteral implements Expression {
	Token token;
	boolean value;

	public BooleanLiteral(Token tok, boolean val) {
		this.token = tok;
		this.value = val;
	}

	@Override
	public void expresionNode() {

	}

	@Override
	public String getNodeValue() {
		return this.token.getTokenValue();
	}

	@Override
	public String getTokenValue() {
		return this.token.getTokenValue();
	}

	@Override
	public void print(String msg) {
		System.out.println(msg + this.getNodeValue());

	}

	void setValue(boolean val) {
		this.value = val;
	}

}
