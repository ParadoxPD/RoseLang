package org.debugger;

import java.util.Vector;
import org.parser.*;
import org.lexer.*;

public class Debugger {
  boolean debug;
  DebugLevel logLevel;

  public Debugger(DebugLevel logLevel) {
    this.debug = logLevel != DebugLevel.NONE;
    this.logLevel = logLevel;
  }

  public void log(String message) {
    if (this.debug) {
      System.out.println(message);
    }
  }

  public void log(String message,DebugLevel level) {
    if (this.logLevel == level) {
      System.out.println(message);
    }
  }

  public void log(String message, Vector<?> objs) {
    if (this.debug) {
      System.out.println(message);
      for (Object obj : objs) {
        switch (obj) {
          case Token tok:
            System.out.println("Object : " + tok + "\nValue: " + tok.printToken());
            break;
          default:
            break;
        }
      }
    }
  }

}


