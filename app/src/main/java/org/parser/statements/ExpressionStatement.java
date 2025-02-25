package org.parser.statements;

import org.lexer.*;
import org.parser.expressions.*;

public class ExpressionStatement implements Statement {
	Token token;
	Expression expression;

	public ExpressionStatement(Token tok, Expression expression) {
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

	public Expression getExpression() {
		return this.expression;
	}

}
