package org.parser.literals;

import java.util.HashMap;
import java.util.Map;
import org.lexer.*;
import org.parser.expressions.*;

public class HashLiteral implements Expression {
  Token token;
  Map<Expression, Expression> elements;

  public HashLiteral(Token tok) {
    this.token = tok;
    this.elements = new HashMap<Expression, Expression>();
  }

  public HashLiteral(Token tok, Map<Expression, Expression> elems) {
    this.token = tok;
    this.elements = elems;
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
    String res = "{ \n";
    for (Expression e : this.elements.keySet()) {
      res += e.getNodeValue() + " : " + this.elements.get(e).getNodeValue() + ",\n";
    }
    res += " }";
    return res;
  }

  // public void setValue(String val) {
  // this.value = val;
  // }

  // public String getValue() {
  // return this.value;
  // }

  @Override
  public String print(String msg) {
    return (msg + this.getNodeValue());
  }

  public Map<Expression, Expression> getElements() {
    return this.elements;
  }

  public void addElement(Expression key, Expression value) {
    this.elements.put(key, value);
  }

  public Expression getElement(Expression key) {
    return this.elements.get(key);
  }
}
