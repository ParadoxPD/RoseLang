package org.parser.statements;

import org.lexer.*;
import org.parser.*;

/** CommentStatement */
public class CommentStatement implements Statement {
    Token token;
    String value;

    public CommentStatement(Token tok, String value) {
        this.token = token;
        this.value = value;
    }

    @Override
    public void statementNode() {
        // TODO Auto-generated method stub

    }

    @Override
    public String getTokenValue() {
        return this.token.getTokenValue();
    }

    @Override
    public String getNodeValue() {
        // TODO Auto-generated method stub
        return this.value;
    }

    @Override
    public String print(String msg) {
        return msg + this.getNodeValue();
    }
}
