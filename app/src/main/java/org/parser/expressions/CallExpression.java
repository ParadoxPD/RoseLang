package org.parser.expressions;

import org.lexer.*;
import java.util.Vector;

public class CallExpression implements Expression {
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

	public Expression getFunction() {
		return this.function;
	}

	public Vector<Expression> getArguments() {
		return this.arguments;
	}
}
