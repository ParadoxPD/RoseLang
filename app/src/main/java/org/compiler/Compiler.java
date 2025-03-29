
package org.compiler;

import org.parser.*;
import org.parser.statements.*;
import org.parser.expressions.*;
import org.parser.literals.*;
import org.error.*;
import org.typesystem.Object_T;
import org.bytecode.utils.*;
import org.bytecode.*;
import org.lexer.*;
import org.debugger.*;
import java.util.*;

public class Compiler {

    byte[] instructions;
    Object_T[] constants;

    Compiler() {
        this.instructions = null;
        this.constants = null;

    }

    void compile(Node node) {
        switch (node) {
            case Program prg:
                for (Statement s : prg.getStatements()) {
                    this.compile(s);

                }
                break;
            case ExpressionStatement es:
                this.compile(es.getExpression());
                break;
            case InfixExpression ie:
                this.compile(ie.getLeft());
                this.compile(ie.getRight());
                break;

            case IntegerLiteral il:
                break;

            default:
                break;
        }

    }

    ByteCode bytecode() {
        return new ByteCode(this.instructions, this.constants);
    }

    Program parse(String input) {
        Debugger debugger = new Debugger(DebugLevel.HIGH);
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
        for (ParserError er : errors) {
            er.printError();
        }
        return program;

    }

    public static void main(String[] args) {

    }

}
