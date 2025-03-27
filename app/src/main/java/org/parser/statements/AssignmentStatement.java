package org.parser.statements;

import org.lexer.*;
import org.parser.*;
import org.parser.expressions.*;

public class AssignmentStatement implements Statement {
  Token token;
  Identifier name;
  Expression expression;

  public AssignmentStatement(Token tok, Identifier name) {
    this.token = tok;
    this.name = name;
  }

  @Override
  public void statementNode() {
    // TODO Auto-generated method stub

  }

  @Override
  public String getNodeValue() {
    return (this.expression != null && this.name != null)
        ? this.name.getNodeValue() + " = " + this.expression.getNodeValue() + ";"
        : "";
  }

  @Override
  public String getTokenValue() {
    return this.token.getTokenValue();
  }

  @Override
  public String print(String msg) {
    return (msg + this.getNodeValue());
  }

  public void setExpression(Expression exp) {
    this.expression = exp;
  }

  public Expression getExpression() {
    return this.expression;
  }

  public Identifier getName() {
    return this.name;
  }
}
