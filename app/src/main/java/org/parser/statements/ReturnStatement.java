package org.parser.statements;

import org.lexer.*;
import org.parser.expressions.*;

public class ReturnStatement implements Statement {

  Token token;
  Expression returnValue;

  public ReturnStatement(Token tok) {
    this.token = tok;
    this.returnValue = null;
  }

  @Override
  public void statementNode() {}

  @Override
  public String getTokenValue() {
    return this.token.getTokenValue();
  }

  @Override
  public String getNodeValue() {
    String res =
        this.token.getTokenValue()
            + " "
            + ((this.returnValue != null) ? this.returnValue.getNodeValue() : "")
            + ";";
    return res;
  }

  @Override
  public String print(String msg) {
    return (msg + this.getNodeValue());
  }

  public Expression getReturnValue() {
    return this.returnValue;
  }

  public void setReturnValue(Expression returnValue) {
    this.returnValue = returnValue;
  }
}
