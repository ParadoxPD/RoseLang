package org.parser;

import org.lexer.Token;

import java.util.Vector;

interface Statement extends Node {
	void statementNode();
}

class ExpressionStatement implements Statement {
	Token token;
	Expression expression;

	ExpressionStatement(Token tok, Expression expression) {
		this.token = tok;
		this.expression = expression;
	}

	@Override
	public void statementNode() {
		// TODO Auto-generated method stub

	}

	@Override
	public String getNodeValue() {
		return (this.expression != null) ? this.expression.getNodeValue() : "";
	}

	@Override
	public String getTokenValue() {
		return this.token.getTokenValue();
	}

	@Override
	public void print(String msg) {
		System.out.println(msg + this.getNodeValue());
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

	@Override
	public String getNodeValue() {
		String res = this.getTokenValue() + " " + this.name.getNodeValue() + " = " + ((this.value != null)
				? this.value.getNodeValue()
				: "") + ";";
		return res;
	}

	@Override
	public void print(String msg) {
		System.out.println(msg + this.getNodeValue());
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

	}

	@Override
	public String getTokenValue() {
		return this.token.getTokenValue();
	}

	@Override
	public String getNodeValue() {
		String res = this.token.getTokenValue() + " " + ((this.returnValue != null)
				? this.returnValue.getNodeValue()
				: "") + ";";
		return res;
	}

	@Override
	public void print(String msg) {
		System.out.println(msg + this.getNodeValue());
	}

	public Expression getReturnValue() {
		return this.returnValue;
	}

	public void setReturnValue(Expression returnValue) {
		this.returnValue = returnValue;
	}

}

class BlockStatement implements Statement {
	Token token;
	Vector<Statement> statements;

	public BlockStatement(Token tok) {
		this.token = tok;
		this.statements = new Vector<Statement>();

	}

	@Override
	public void statementNode() {

	}

	@Override
	public String getTokenValue() {
		return this.token.getTokenValue();
	}

	@Override
	public String getNodeValue() {
		String res = "{ ";
		for (Statement stm : this.statements) {
			res += stm.getNodeValue();
		}
		res += " } ";
		return res;
	}

	@Override
	public void print(String msg) {
		System.out.println(msg + " " + this.getNodeValue());
	}

	public void addStatement(Statement stm) {
		this.statements.addElement(stm);
	}
}
