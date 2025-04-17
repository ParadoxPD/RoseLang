package org.commandline;

import org.code.utils.Helper;
import org.compiler.*;
import org.debugger.DebugLevel;
import org.debugger.Debugger;
import org.error.CompilerError;
import org.error.ParserError;
import org.error.VMError;
import org.lexer.*;
import org.parser.*;
import org.repl.*;
import org.repl.utils.*;
import org.typesystem.Null_T;
import org.typesystem.Object_T;
import org.vm.VM;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

public class CommandLine {
    public static void main(String args[]) {

        Map<String, String> parsedArgs = new HashMap<String, String>();
        setDefaults(parsedArgs);

        for (String arg : args) {
            if (arg.contains("=")) parsedArgs.put(arg.split("=")[0], arg.split("=")[1]);
            else parsedArgs.put(arg, "");
        }
        for (String arg : parsedArgs.keySet()) {
            switch (arg) {
                case "--repl":
                    REPL.run(parsedArgs);
                    break;
                case "--compile":
                    System.out.println("Compile");
                    break;
                case "--help":
                    printHelp();
                    break;
                case "--old-repl":
                    REPL.eval(parsedArgs);
                    break;
                case "--eval":
                    if (!parsedArgs.containsKey("--file")) {
                        System.out.println(
                                "Why you do this? What will I eval? Give a file to eval");
                        return;
                    }
                    String filename = "../test-programs/" + parsedArgs.get("--file");

                    try {
                        String content = Files.readString(Path.of(filename));
                        run(parsedArgs, content);

                    } catch (IOException io) {
                        System.err.println(io);
                        System.out.println("Something wrong");
                    }
                    break;
            }
        }
    }

    static void printHelp() {
        String help =
                "--complile -> Compiles the Jork code\n"
                        + "--relp -> Start Jork REPL\n"
                        + "--output -> output file\n"
                        + "--debug -> Print Debug info";
        System.out.println(help);
    }

    static void setDefaults(Map<String, String> parsedArgs) {
        parsedArgs.put("--debug", "false");
    }

    public static void run(Map<String, String> parsedArgs, String contents) {
        boolean debug = Boolean.parseBoolean(parsedArgs.get("--debug"));
        System.out.println(debug);
        Debugger debugger = new Debugger(debug ? DebugLevel.HIGH : DebugLevel.NONE);
        Vector<Object_T> constants = Helper.<Object_T>createVector();
        Vector<Object_T> globals = Helper.<Object_T>createVector(VM.GlobalsSize, null);
        SymbolTable symbolTable = new SymbolTable();
        Lexer lx = new Lexer(contents, debugger);

        debugger.log(contents);

        lx.tokenize();
        lx.printTokens();
        Vector<Token> tokens = lx.getTokens();
        debugger.log("\n\n\n\n----------Parsing------------\n\n\n");

        Parser ps = new Parser(tokens, debugger);

        Program program = ps.parseProgram();
        ps.printProgram();
        Vector<ParserError> errors = ps.getErrors();
        if (errors.size() == 0 && program != null) {

            Compiler cmp = new Compiler(symbolTable, constants, debugger);
            CompilerError err = cmp.compile(program);
            debugger.log("Compiled Instructions : ");
            cmp.printIns();
            if (err != null) {
                err.printError();
                return;
            }

            VM machine = new VM(cmp.bytecode(), globals, debugger);
            VMError v_err = machine.run();
            if (v_err != null) {
                v_err.printError();
                return;
            }
            Object_T stackTop = machine.lastPoppedStackElement();
            if (!(stackTop instanceof Null_T)) System.out.println(stackTop.inspect());

        } else {

            for (ParserError er : errors) {
                er.printError();
            }
        }
    }
}
