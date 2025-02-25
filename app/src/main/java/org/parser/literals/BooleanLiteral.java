package org.parser.literals;

import org.lexer.*;
import org.parser.expressions.*;

public class BooleanLiteral implements Expression {
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

	public void setValue(boolean val) {
		this.value = val;
	}

	public boolean getValue() {
		return this.value;
	}

}
