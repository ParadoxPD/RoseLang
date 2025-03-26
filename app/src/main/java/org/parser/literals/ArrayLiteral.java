package org.parser.literals;

import java.util.Vector;

import org.lexer.*;
import org.parser.expressions.*;

public class ArrayLiteral implements Expression {
	Token token;
	Vector<Expression> elements;

	public ArrayLiteral(Token tok) {
		this.token = tok;
		this.elements = new Vector<Expression>();

	}

	public ArrayLiteral(Token tok, Vector<Expression> elems) {
		this.token = tok;
		this.elements = elems;
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
		String res = "[ ";
		for (Expression e : this.elements) {
			res += e.getNodeValue() + ", ";
		}
		res += " ]";
		return res;
	}

	// public void setValue(String val) {
	// this.value = val;
	// }

	// public String getValue() {
	// return this.value;
	// }

	@Override
	public void print(String msg) {
		System.out.println(msg + this.getNodeValue());
	}

	public Vector<Expression> getElements() {
		return this.elements;
	}

}
