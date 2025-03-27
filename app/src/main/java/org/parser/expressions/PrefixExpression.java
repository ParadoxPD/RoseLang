package org.parser.expressions;

import org.lexer.*;

public class PrefixExpression implements Expression {
  private Token token;
  private String operator;
  private Expression right;

  public PrefixExpression(Token tok, String op) {
    this.token = tok;
    this.operator = op;
  }

  @Override
  public void expressionNode() {}

  @Override
  public String getTokenValue() {
    return this.token.getTokenValue();
  }

  @Override
  public String getNodeValue() {
    return "(" + this.operator + this.right.getNodeValue() + ")";
  }

  @Override
  public String print(String msg) {
    return (msg + this.getNodeValue());
  }

  public void setRight(Expression right) {
    this.right = right;
  }

  public Expression getRight() {
    return this.right;
  }

  public String getOperator() {
    return this.operator;
  }
}
