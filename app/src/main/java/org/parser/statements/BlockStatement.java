package org.parser.statements;

import org.lexer.*;
import java.util.Vector;

public class BlockStatement implements Statement {
	Token token;
	Vector<Statement> statements;

	public BlockStatement(Token tok) {
		this.token = tok;
		this.statements = new Vector<Statement>();

	}

	@Override
	public void statementNode() {

	}

	@Override
	public String getTokenValue() {
		return this.token.getTokenValue();
	}

	@Override
	public String getNodeValue() {
		String res = "{ ";
		for (Statement stm : this.statements) {
			res += stm.getNodeValue();
		}
		res += " } ";
		return res;
	}

	@Override
	public void print(String msg) {
		System.out.println(msg + " " + this.getNodeValue());
	}

	public void addStatement(Statement stm) {
		this.statements.addElement(stm);
	}

	public Vector<Statement> getStatements() {
		return this.statements;
	}
}
