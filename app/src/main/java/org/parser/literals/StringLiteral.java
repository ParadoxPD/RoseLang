package org.parser.literals;

import org.lexer.*;
import org.parser.expressions.*;

public class StringLiteral implements Expression {
	Token token;
	String value;

	public StringLiteral(Token tok) {
		this.token = tok;

	}

	public StringLiteral(Token tok, String val) {
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

	public void setValue(String val) {
		this.value = val;
	}

	public String getValue() {
		return this.value;
	}

	@Override
	public void print(String msg) {
		System.out.println(msg + this.getNodeValue());
	}

}
