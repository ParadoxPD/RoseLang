package org.parser;

import org.lexer.Token;

import java.util.Vector;

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
	public void expressionNode() {
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
	public void expressionNode() {

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

class FunctionLiteral implements Expression {
	Token token;
	Identifier name;
	Vector<Identifier> parameters;
	BlockStatement body;

	public FunctionLiteral(Token tok) {
		this.token = tok;
		this.parameters = new Vector<Identifier>();
	}

	@Override
	public void expressionNode() {

	}

	@Override
	public String getTokenValue() {
		return this.token.getTokenValue();
	}

	@Override
	public String getNodeValue() {
		String res = this.getTokenValue() + " " + this.name.getNodeValue() + "( ";
		for (Identifier i : this.parameters) {
			res += i.getNodeValue() + ", ";
		}
		res += ") " + this.body.getNodeValue();
		return res;

	}

	@Override
	public void print(String msg) {
		System.out.println(msg + this.getNodeValue());
	}

	public void setName(Identifier name) {
		this.name = name;
	}

	public void addParameter(Identifier parameter) {
		this.parameters.addElement(parameter);
	}

	public void addParameters(Vector<Identifier> parameters) {
		this.parameters = parameters;
	}

	public void addBody(BlockStatement body) {
		this.body = body;
	}

}
