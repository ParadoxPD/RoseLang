package org.parser;

import org.lexer.Token;

import java.util.Vector;

interface Expression extends Node {

	void expressionNode();
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
	public void expressionNode() {
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
	public void expressionNode() {

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

class IfExpression implements Expression {
	Token token;
	Expression condition;
	BlockStatement consequence;
	BlockStatement alternative;

	public IfExpression(Token tok) {
		this.token = tok;
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
		// return "";
		return "if " + this.condition.getNodeValue() + this.consequence.getNodeValue()
				+ ((this.alternative != null) ? ("else" + this.alternative.getNodeValue())
						: "");
	}

	@Override
	public void print(String msg) {
		System.out.println(msg + " " + this.getNodeValue());
	}

	public void setCondition(Expression condition) {
		this.condition = condition;
	}

	public void setConsequence(BlockStatement consequence) {
		this.consequence = consequence;
	}

	public void setAlternative(BlockStatement alternative) {
		this.alternative = alternative;
	}
}

class CallExpression implements Expression {
	Token token;
	Expression function;
	Vector<Expression> arguments;

	public CallExpression(Token tok, Expression function) {
		this.token = tok;
		this.function = function;
		this.arguments = new Vector<Expression>();
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
		String res = this.function.getNodeValue() + "( ";

		for (Expression arg : arguments) {
			res += arg.getNodeValue() + ", ";
		}
		res += " )";
		return res;
	}

	@Override
	public void print(String msg) {
		System.out.println(msg + this.getNodeValue());
	}

	public void addArguments(Vector<Expression> args) {
		this.arguments = args;
	}
}
