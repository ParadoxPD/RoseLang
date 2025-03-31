package org.commandline;

import java.util.*;

import org.repl.*;
import org.compiler.*;
import org.debugger.DebugLevel;
import org.debugger.Debugger;
import org.environment.Environment;
import org.error.ParserError;
import org.evaluator.Evaluator;
import org.lexer.*;
import org.parser.*;
import org.compiler.*;
import org.typesystem.Object_T;
import org.vm.VM;

public class CommandLine {
    public static void main(String args[]) {

        Map<String, String> parsedArgs = new HashMap<String, String>();
        setDefaults(parsedArgs);

        for (String arg : args) {
            if (arg.contains("="))
                parsedArgs.put(arg.split("=")[0], arg.split("=")[1]);
            else
                parsedArgs.put(arg, "");
        }
        if (parsedArgs.containsKey("--repl")) {
            REPL.run(parsedArgs);
        } else if (parsedArgs.containsKey("--compile")) {
            System.out.println("Compile");
            Compiler.main(args);
        } else if (parsedArgs.containsKey("--help")) {
            printHelp();
        } else if (parsedArgs.containsKey("--eval")) {
            eval(parsedArgs);
        } else {
            System.out.println("You fucked up");
        }

    }

    static void printHelp() {
        String help = "--complile -> Compiles the Jork code\n--relp -> Start Jork REPL\n--output -> output file\n--debug -> Print Debug info";
        System.out.println(help);
    }

    static void setDefaults(Map<String, String> parsedArgs) {
        parsedArgs.put("--debug", "false");
    }

    static void eval(Map<String, String> parsedArgs) {
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
}
