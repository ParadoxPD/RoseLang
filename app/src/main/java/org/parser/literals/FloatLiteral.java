package org.parser.literals;

import org.lexer.*;
import org.parser.expressions.*;

public class FloatLiteral implements Expression {
  Token token;
  float value;

  public FloatLiteral(Token tok) {
    this.token = tok;
  }

  public FloatLiteral(Token tok, float val) {
    this.token = tok;
    this.value = val;
  }

  @Override
  public void expressionNode() {
    // TODO Auto-generated method stub

  }

  @Override
  public String getTokenValue() {
    return this.token.getTokenValue();
  }

  @Override
  public String getNodeValue() {
    return this.token.getTokenValue();
  }

  public void setValue(float val) {
    this.value = val;
  }

  public float getValue() {
    return this.value;
  }

  @Override
  public String print(String msg) {
    return (msg + this.getNodeValue());
  }
}
