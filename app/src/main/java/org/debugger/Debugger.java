package org.debugger;

import java.util.Vector;

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

  public void log(String message, Vector<Object> objs) {
    if (this.debug) {
      System.out.println(message);
      for (Object obj : objs) {
        System.out.println("Object : " + obj + "\nClass" + obj.getClass());
      }
    }
  }
}
