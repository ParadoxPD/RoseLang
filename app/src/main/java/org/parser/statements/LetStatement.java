package org.parser.statements;

import org.lexer.*;
import org.parser.*;
import org.parser.expressions.*;

public class LetStatement implements Statement {
  Token token;
  Identifier name;
  Expression value;

  public LetStatement(Token tok) {
    this.token = tok;
    name = null;
    value = null;
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
        this.getTokenValue()
            + " "
            + this.name.getNodeValue()
            + " = "
            + ((this.value != null) ? this.value.getNodeValue() : "")
            + ";";
    return res;
  }

  @Override
  public String print(String msg) {
    return (msg + this.getNodeValue());
  }

  public Identifier getName() {
    return this.name;
  }

  public Expression getValue() {
    return this.value;
  }

  public void setName(Identifier name) {
    this.name = name;
  }

  public void setValue(Expression value) {
    this.value = value;
  }
}
