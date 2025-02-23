package org.parser;

import org.lexer.Token;

import java.util.Vector;

interface Node {
	String getTokenValue();

	void print(String msg);

	String getNodeValue();

}

interface Statement extends Node {
	void statementNode();
}

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

	}

	@Override
	public String getNodeValue() {
		return this.value;

	}

	@Override
	public void print(String msg) {
		System.out.println(msg + this.getNodeValue());
	}

}

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
		String res = this.getTokenValue() + this.name.getNodeValue() + " = " + ((this.value != null)
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

class Program implements Node {

	// TODO: Refactor the statements to have some kind of getter and setter
	Vector<Statement> statements;

	public Program() {
		this.statements = new Vector<Statement>();
	}

	@Override
	public String getTokenValue() {
		if (this.statements.isEmpty()) {
			return "";
		}
		return this.statements.get(0).getTokenValue();
	}

	@Override
	public String getNodeValue() {
		String res = "";
		for (Statement stm : this.statements) {
			res += stm.getNodeValue();
		}
		return res;
	}

	@Override
	public void print(String msg) {
		System.out.println(msg + this.getNodeValue());
	}
}

public class AST {

}
