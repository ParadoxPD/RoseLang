package org.parser;

import java.util.Vector;
import org.parser.statements.*;

public class Program implements Node {

  // TODO: Refactor the statements to have some kind of getter and setter
  Vector<Statement> statements;

  public Program() {
    this.statements = new Vector<Statement>();
  }

  @Override
  public String getTokenValue() {
    if (this.statements.isEmpty()) {
      return "";
    }
    return this.statements.get(0).getTokenValue();
  }

  @Override
  public String getNodeValue() {
    String res = "";
    for (Statement stm : this.statements) {
      res += stm.getNodeValue();
    }
    return res;
  }

  @Override
  public String print(String msg) {
    return (msg + this.getNodeValue());
  }

  public Vector<Statement> getStatements() {
    return this.statements;
  }
}
