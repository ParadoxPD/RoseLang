
package org.compiler;

import org.parser.*;
import org.parser.statements.*;
import org.parser.expressions.*;
import org.parser.literals.*;
import org.error.*;
import org.typesystem.*;
import org.vm.VM;
import org.code.utils.*;
import org.code.*;
import org.lexer.*;
import org.debugger.*;
import java.util.*;

public class Compiler {

    Vector<Byte> instructions;
    Vector<Object_T> constants;

    public Compiler() {
        this.instructions = new Vector<Byte>();
        this.constants = new Vector<Object_T>();

    }

    int addConstant(Object_T obj) {
        this.constants.add(obj);
        return this.constants.size() - 1;
    }

    public void compile(Node node) {
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

                switch (ie.getOperator()) {
                    case "+":
                        this.emit(OpCodes.OpAdd);
                    default:
                        break;
                }
                break;

            case IntegerLiteral il:
                Integer_T integer = new Integer_T(il.getValue());
                this.emit(OpCodes.OpConstant, this.addConstant(integer));
                break;

            default:
                break;
        }

    }

    int emit(byte op, int... operands) {
        byte[] ins = new Code().make(op, operands);
        int pos = this.addInstuctions(ins);
        return pos;
    }

    int addInstuctions(byte[] ins) {
        int pos = this.instructions.size();
        for (byte in : ins)
            this.instructions.add(in);
        return pos;

    }

    public ByteCode bytecode() {
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
        Compiler cmp = new Compiler();
        cmp.compile(cmp.parse("1+2"));
        System.out.println(new Code().toString(cmp.instructions));
        VM vm = new VM(cmp.bytecode());
        vm.run();
        System.out.println(vm.stackTop().inspect());

    }

}
