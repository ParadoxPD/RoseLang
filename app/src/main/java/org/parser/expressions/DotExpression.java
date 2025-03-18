package org.parser.expressions;

import org.lexer.*;

public class DotExpression extends InfixExpression {

	public DotExpression(Token tok, Expression left) {
		super(tok, TokenList.DOT, left);
	}

	@Override
	public void expressionNode() {

	}

	@Override
	public String getTokenValue() {
		return super.getTokenValue();
	}

	@Override
	public String getNodeValue() {
		return super.getNodeValue();
	}

	@Override
	public void print(String msg) {
		super.print(msg);
	}

	public void setRight(Expression exp) {
		super.setRight(exp);
	}

	public Expression getLeft() {
		return super.getLeft();
	}

	public Expression getRight() {
		return super.getRight();
	}

	public String getOperator() {
		return super.getOperator();
	}
}
