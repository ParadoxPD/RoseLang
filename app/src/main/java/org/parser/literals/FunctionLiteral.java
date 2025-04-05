package org.parser.literals;

import org.lexer.*;
import org.parser.*;
import org.parser.expressions.*;
import org.parser.statements.*;

import java.util.Vector;

public class FunctionLiteral implements Statement {
    Token token;
    Identifier name;
    Vector<Identifier> parameters;
    BlockStatement body;

    public FunctionLiteral(Token tok) {
        this.token = tok;
        this.parameters = new Vector<Identifier>();
    }

    @Override
    public void statementNode() {}

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
    public String print(String msg) {
        return (msg + this.getNodeValue());
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

    public Identifier getName() {
        return this.name;
    }

    public BlockStatement getBody() {
        return this.body;
    }

    public Vector<Identifier> getParameters() {
        return this.parameters;
    }
}
