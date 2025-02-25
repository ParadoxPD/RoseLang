package org.parser.literals;

import org.lexer.*;
import org.parser.statements.*;
import org.parser.expressions.*;
import org.parser.*;
import java.util.Vector;

public class FunctionLiteral implements Expression {
	Token token;
	Identifier name;
	Vector<Identifier> parameters;
	BlockStatement body;

	public FunctionLiteral(Token tok) {
		this.token = tok;
		this.parameters = new Vector<Identifier>();
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
		String res = this.getTokenValue() + " " + this.name.getNodeValue() + "( ";
		for (Identifier i : this.parameters) {
			res += i.getNodeValue() + ", ";
		}
		res += ") " + this.body.getNodeValue();
		return res;

	}

	@Override
	public void print(String msg) {
		System.out.println(msg + this.getNodeValue());
	}

	public void setName(Identifier name) {
		this.name = name;
	}

	public void addParameter(Identifier parameter) {
		this.parameters.addElement(parameter);
	}

	public void addParameters(Vector<Identifier> parameters) {
		this.parameters = parameters;
	}

	public void addBody(BlockStatement body) {
		this.body = body;
	}

}
