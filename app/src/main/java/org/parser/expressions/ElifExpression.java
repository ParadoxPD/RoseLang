package org.parser.expressions;

import org.lexer.*;
import org.parser.statements.*;

/** ElifStatement */
public class ElifExpression implements Expression {
    Token token;
    Expression condition;
    BlockStatement consequence;

    public ElifExpression(Token tok) {
        this.token = tok;
    }

    @Override
    public void expressionNode() {}

    @Override
    public String getTokenValue() {
        return this.token.getTokenValue();
    }

    @Override
    public String getNodeValue() {
        // return "";
        return "elif " + this.condition.getNodeValue() + this.consequence.getNodeValue();
    }

    @Override
    public String print(String msg) {
        return (msg + " " + this.getNodeValue());
    }

    public void setCondition(Expression condition) {
        this.condition = condition;
    }

    public void setConsequence(BlockStatement consequence) {
        this.consequence = consequence;
    }

    public Expression getCondition() {
        return this.condition;
    }

    public BlockStatement getConsequence() {
        return this.consequence;
    }
}
