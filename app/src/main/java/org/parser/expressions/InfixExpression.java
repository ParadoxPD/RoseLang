package org.parser.expressions;

import org.lexer.*;

public class InfixExpression implements Expression {
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

	public Expression getRight() {
		return this.right;
	}

	public String getOperator() {
		return this.operator;
	}
}
