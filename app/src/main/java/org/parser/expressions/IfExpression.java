package org.parser.expressions;

import org.lexer.*;
import org.parser.statements.*;

public class IfExpression implements Expression {
	Token token;
	Expression condition;
	BlockStatement consequence;
	BlockStatement alternative;

	public IfExpression(Token tok) {
		this.token = tok;
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
		// return "";
		return "if " + this.condition.getNodeValue() + this.consequence.getNodeValue()
				+ ((this.alternative != null) ? ("else" + this.alternative.getNodeValue())
						: "");
	}

	@Override
	public void print(String msg) {
		System.out.println(msg + " " + this.getNodeValue());
	}

	public void setCondition(Expression condition) {
		this.condition = condition;
	}

	public void setConsequence(BlockStatement consequence) {
		this.consequence = consequence;
	}

	public void setAlternative(BlockStatement alternative) {
		this.alternative = alternative;
	}
}
