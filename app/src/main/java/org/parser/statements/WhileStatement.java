package org.parser.statements;

import org.lexer.*;
import org.parser.expressions.*;;

public class WhileStatement implements Statement {
	Token token;
	Expression condition;
	BlockStatement body;

	public WhileStatement(Token tok) {
		this.token = tok;
		this.condition = null;
		this.body = null;
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
		// return "";
		return "while " + this.condition.getNodeValue() + this.body.getNodeValue();
	}

	@Override
	public void print(String msg) {
		System.out.println(msg + " " + this.getNodeValue());
	}

	public void setCondition(Expression condition) {
		this.condition = condition;
	}

	public void setBody(BlockStatement body) {
		this.body = body;
	}

	public Expression getCondition() {
		return this.condition;
	}

	public BlockStatement getBody() {
		return this.body;
	}

}
