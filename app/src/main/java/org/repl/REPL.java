package org.repl;

import java.util.Map;
import java.util.Scanner;
import java.util.Vector;
import org.debugger.DebugLevel;
import org.debugger.Debugger;
import org.environment.Environment;
import org.error.CompilerError;
import org.error.ParserError;
import org.error.VMError;
import org.evaluator.Evaluator;
import org.lexer.*;
import org.parser.*;
import org.compiler.*;
import org.typesystem.Object_T;
import org.vm.VM;
import org.repl.utils.*;

public class REPL {

  public static void run(Map<String, String> parsedArgs) {
    Scanner sc = new Scanner(System.in);
    boolean debug = Boolean.parseBoolean(parsedArgs.get("--debug"));
    System.out.println(debug);
    Debugger debugger = new Debugger(debug ? DebugLevel.HIGH : DebugLevel.NONE);
    Vector<Object_T> constants = new Vector<Object_T>();
    Vector<Object_T> globals = new Vector<Object_T>(VM.GlobalsSize);
    SymbolTable symbolTable = new SymbolTable();

    String prompt = ">> ";
    System.out.println(
        "Welcome to JorkLang where you can jork the lang...\nEnter the commands below");

    while (true) {
      System.out.print(prompt);
      String input = sc.nextLine();
      if (input == null || input.equals("")) {
        continue;
      }
      switch (input) {
        case REPLKeywords.exitK:
          return;
        case REPLKeywords.helpK:
          help();
          continue;
        case REPLKeywords.clearK:
          System.out.print("\033[H\033[2J");
          continue;
        default:
          Lexer lx = new Lexer(input, debugger);
          lx.tokenize();
          lx.printTokens();
          Vector<Token> tokens = lx.getTokens();
          debugger.log("\n\n\n\n----------Parsing------------\n\n\n");
          Parser ps = new Parser(tokens, debugger);
          ps.parseProgram();
          ps.printProgram();
          Program program = ps.getProgram();
          Vector<ParserError> errors = ps.getErrors();
          if (errors.size() == 0) {

            Compiler cmp = new Compiler(symbolTable, constants);
            CompilerError err = cmp.compile(program);
            cmp.printIns();
            if (err != null) {
              err.printError();
              continue;
            }

            VM machine = new VM(cmp.bytecode(), globals);
            VMError v_err = machine.run();
            if (v_err != null) {
              v_err.printError();
              continue;
            }
            Object_T stackTop = machine.lastPoppedStackElement();
            System.out.println(stackTop.inspect());

          } else {

            for (ParserError er : errors) {
              er.printError();
            }
          }
          break;
      }
    }

  }

  public static void eval(Map<String, String> parsedArgs) {
    Scanner sc = new Scanner(System.in);
    boolean debug = Boolean.parseBoolean(parsedArgs.get("--debug"));
    System.out.println(debug);
    Debugger debugger = new Debugger(debug ? DebugLevel.HIGH : DebugLevel.NONE);
    String prompt = ">> ";
    System.out.println(
        "Welcome to JorkLang where you can jork the lang...\nEnter the commands below");

    Environment globalEnv = new Environment();
    Evaluator evaluator = new Evaluator();
    while (true && globalEnv != null) {
      System.out.print(prompt);
      String input = sc.nextLine();
      if (input == null || input.equals("")) {
        continue;
      }
      if (input.equals("exit")) {
        sc.close();
        break;
      }
      Lexer lx = new Lexer(input, debugger);
      lx.tokenize();
      lx.printTokens();
      Vector<Token> tokens = lx.getTokens();
      debugger.log("\n\n\n\n----------Parsing------------\n\n\n");
      Parser ps = new Parser(tokens, debugger);
      ps.parseProgram();
      ps.printProgram();
      Program program = ps.getProgram();
      Vector<ParserError> errors = ps.getErrors();
      if (errors.size() == 0) {
        Object_T object = evaluator.eval(program, globalEnv);

        if (object != null) {
          System.out.println(object.inspect());

        } else {
          System.out.println("Unable to evaluate expression");
        }
      } else {

        for (ParserError er : errors) {
          er.printError();
        }
      }
    }

  }

  public static void help() {
    System.out.println("HELP");
  }

}
