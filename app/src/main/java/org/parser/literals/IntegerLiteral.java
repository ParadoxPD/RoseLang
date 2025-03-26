package org.parser.literals;

import org.lexer.*;
import org.parser.expressions.*;

public class IntegerLiteral implements Expression {
	Token token;
	int value;

	public IntegerLiteral(Token tok) {
		this.token = tok;

	}

	public IntegerLiteral(Token tok, int val) {
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

	public int getValue() {
		return this.value;
	}

	@Override
	public void print(String msg) {
		System.out.println(msg + this.getNodeValue());
	}

}
