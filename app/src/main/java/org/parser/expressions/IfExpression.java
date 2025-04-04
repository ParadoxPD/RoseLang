package org.parser.expressions;

import org.lexer.*;
import org.parser.statements.*;

import java.util.*;

public class IfExpression implements Expression {
    Token token;
    Expression condition;
    BlockStatement consequence;
    Vector<ElifExpression> elifExpressions;
    BlockStatement alternative;

    public IfExpression(Token tok) {
        this.token = tok;
        this.elifExpressions = new Vector<ElifExpression>();
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
        String res = "if " + this.condition.getNodeValue() + this.consequence.getNodeValue();

        for (ElifExpression elf : this.elifExpressions) {
            res += elf.getNodeValue();
        }

        res += ((this.alternative != null) ? ("else" + this.alternative.getNodeValue()) : "");
        return res;
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

    public void setAlternative(BlockStatement alternative) {
        this.alternative = alternative;
    }

    public Expression getCondition() {
        return this.condition;
    }

    public BlockStatement getConsequence() {
        return this.consequence;
    }

    public BlockStatement getAlternative() {
        return this.alternative;
    }

    public void addElifExpression(ElifExpression exp) {
        this.elifExpressions.add(exp);
    }

    public Vector<ElifExpression> getElifExpression() {
        return this.elifExpressions;
    }

    public boolean elifExists() {
        return this.elifExpressions.size() > 0;
    }
}
