package org.parser;

import org.parser.statements.*;
import java.util.Vector;

public class Program implements Node {

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

	public Vector<Statement> getStatements() {
		return this.statements;
	}
}
