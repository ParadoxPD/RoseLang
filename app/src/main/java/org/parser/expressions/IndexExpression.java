package org.parser.expressions;

import org.lexer.*;

public class IndexExpression implements Expression {
  private Token token;
  private Expression left;
  private Expression index;

  public IndexExpression(Token tok, Expression left) {
    this.token = tok;
    this.left = left;
  }

  @Override
  public void expressionNode() {}

  @Override
  public String getTokenValue() {
    return this.token.getTokenValue();
  }

  @Override
  public String getNodeValue() {
    return "(" + this.left.getNodeValue() + "[" + this.index.getNodeValue() + "])";
  }

  @Override
  public String print(String msg) {
    return (msg + this.getNodeValue());
  }

  public void setLeft(Expression left) {
    this.left = left;
  }

  public Expression getLeft() {
    return this.left;
  }

  public Expression getIndex() {
    return this.index;
  }

  public void setIndex(Expression index) {
    this.index = index;
  }
}
