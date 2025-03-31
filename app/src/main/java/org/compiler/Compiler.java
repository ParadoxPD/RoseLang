
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

    EmittedInstruction lastInstruction;
    EmittedInstruction previousInstruction;

    public Compiler() {
        this.instructions = new Vector<Byte>();
        this.constants = new Vector<Object_T>();
        lastInstruction = new EmittedInstruction();
        previousInstruction = new EmittedInstruction();

    }

    int addConstant(Object_T obj) {
        this.constants.add(obj);
        return this.constants.size() - 1;
    }

    public void compile(Node node) {
        switch (node) {
            // Program
            // Statements
            // Expressions
            // Literals
            case Program prg:
                for (Statement s : prg.getStatements()) {
                    this.compile(s);

                }
                break;
            case ExpressionStatement es:
                this.compile(es.getExpression());
                this.emit(OpCodes.OpPop);
                break;
            case BlockStatement bs:
                for (Statement s : bs.getStatements()) {
                    this.compile(s);

                }
                break;
            case IfExpression ie:

                this.compile(ie.getCondition());

                int jumpNotTruthyPos = this.emit(OpCodes.OpJumpNotTruthy, 9999);
                this.compile(ie.getConsequence());

                if (this.lastInstructionIsPop()) {
                    this.removeLastPop();
                }

                int jumpPos = this.emit(OpCodes.OpJump, 9999);
                int afterConsequencePos = this.instructions.size();
                this.changeOperand(jumpNotTruthyPos, afterConsequencePos);

                if (ie.getAlternative() == null) {
                    this.emit(OpCodes.OpNull);
                } else {

                    this.compile(ie.getAlternative());

                    if (this.lastInstructionIsPop()) {
                        this.removeLastPop();
                    }
                }

                int afterAlternativePos = this.instructions.size();
                this.changeOperand(jumpPos, afterAlternativePos);

                break;
            case InfixExpression ie:
                System.out.println("Operator :" + ie.getOperator());

                if (ie.getOperator().equals("<") || ie.getOperator().equals("<=")) {
                    this.compile(ie.getRight());
                    this.compile(ie.getLeft());
                    System.out.println("Ahhhhhhhh");
                    if (ie.getOperator().equals("<"))
                        this.emit(OpCodes.OpGreaterThan);
                    else
                        this.emit(OpCodes.OpGreaterThanEqualTo);
                    return;
                }
                this.compile(ie.getLeft());
                this.compile(ie.getRight());

                switch (ie.getOperator()) {
                    case "+":
                        this.emit(OpCodes.OpAdd);
                        break;
                    case "-":
                        this.emit(OpCodes.OpSub);
                        break;
                    case "*":
                        this.emit(OpCodes.OpMul);
                        break;
                    case "/":
                        this.emit(OpCodes.OpDiv);
                        break;
                    case "^":
                        this.emit(OpCodes.OpPow);
                        break;
                    case ">":
                        this.emit(OpCodes.OpGreaterThan);
                        break;
                    case ">=":
                        this.emit(OpCodes.OpGreaterThanEqualTo);
                        break;
                    case "==":
                        this.emit(OpCodes.OpEqual);
                        break;
                    case "!=":
                        this.emit(OpCodes.OpNotEqual);
                        break;
                    default:
                        break;
                }
                break;
            case PrefixExpression pe:
                this.compile(pe.getRight());

                switch (pe.getOperator()) {
                    case "!":
                        this.emit(OpCodes.OpBang);
                        break;
                    case "-":
                        this.emit(OpCodes.OpMinus);
                        break;
                    default:
                        break;
                }
                break;
            case IntegerLiteral il:
                Integer_T integer = new Integer_T(il.getValue());
                this.emit(OpCodes.OpConstant, this.addConstant(integer));
                break;

            case BooleanLiteral bl:
                if (bl.getValue())
                    this.emit(OpCodes.OpTrue);
                else
                    this.emit(OpCodes.OpFalse);
                break;

            default:
                break;
        }

    }

    boolean lastInstructionIsPop() {
        return this.lastInstruction.opCode == OpCodes.OpPop;
    }

    void removeLastPop() {
        this.instructions = Helper.slice(this.instructions, 0, this.lastInstruction.position);
        this.lastInstruction = this.previousInstruction;
    }

    int emit(byte op, int... operands) {
        byte[] ins = new Code().make(op, operands);
        int pos = this.addInstuctions(ins);

        this.setLastInstruction(op, pos);
        return pos;
    }

    int addInstuctions(byte[] ins) {
        int pos = this.instructions.size();
        for (byte in : ins)
            this.instructions.add(in);
        return pos;

    }

    void replaceInstruction(int pos, byte[] newInstruction) {
        for (int i = 0; i < newInstruction.length; i++) {
            this.instructions.set(pos + i, newInstruction[i]);
        }
    }

    void changeOperand(int opPos, int operand) {
        byte op = this.instructions.get(opPos);
        byte[] newInstruction = new Code().make(op, operand);

        this.replaceInstruction(opPos, newInstruction);
    }

    void setLastInstruction(byte op, int pos) {
        EmittedInstruction previous = this.lastInstruction;
        EmittedInstruction last = new EmittedInstruction(op, pos);

        this.previousInstruction = previous;
        this.lastInstruction = last;
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

class EmittedInstruction {
    byte opCode;
    int position;

    EmittedInstruction() {
        this.opCode = 0;
        this.position = 0;
    }

    EmittedInstruction(byte op, int pos) {
        this.opCode = op;
        this.position = pos;
    }
}
