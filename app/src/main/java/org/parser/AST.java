package org.parser;

import org.lexer.Token;

import java.util.Vector;

interface Node {
	String getTokenValue();

	void print(String msg);

	String getNodeValue();

}

class Program implements Node {

	// TODO: Refactor the statements to have some kind of getter and setter
	Vector<Statement> statements;

	public Program() {
		this.statements = new Vector<Statement>();
	}

	@Override
	public String getTokenValue() {
		if (this.statements.isEmpty()) {
			return "";
		}
		return this.statements.get(0).getTokenValue();
	}

	@Override
	public String getNodeValue() {
		String res = "";
		for (Statement stm : this.statements) {
			res += stm.getNodeValue();
		}
		return res;
	}

	@Override
	public void print(String msg) {
		System.out.println(msg + this.getNodeValue());
	}
}

public class AST {

}
