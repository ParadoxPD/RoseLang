package org.vm;

import java.util.Vector;

import org.code.*;
import org.error.*;
import org.code.utils.*;
import org.compiler.*;
import org.typesystem.*;
import org.typesystem.utils.*;

/**
 * VM
 */
public class VM {

    public final static int StackSize = 2048;
    public final static int GlobalsSize = 65536;

    Vector<Object_T> constants;
    Vector<Byte> instructions;

    Vector<Object_T> stack;
    Vector<Object_T> globals;
    int sp;

    public VM(ByteCode bytecode) {
        this.instructions = bytecode.getInstructions();
        this.constants = bytecode.getConstants();

        this.stack = new Vector<Object_T>(StackSize);
        this.globals = new Vector<Object_T>(GlobalsSize);
        this.sp = 0;
    }

    public VM(ByteCode bytecode, Vector<Object_T> globals) {
        this(bytecode);
        this.globals = globals;
    }

    public Object_T stackTop() {
        if (this.sp == 0) {
            return null;
        }

        return this.stack.get(this.sp - 1);
    }

    public VMError run() {
        VMError err = null;
        for (int insPointer = 0; insPointer < this.instructions.size(); insPointer++) {
            byte op = this.instructions.get(insPointer);
            System.out.println("Operator : " + op);
            switch (op) {
                case OpCodes.OpConstant:
                    int constIndex = Code.readUint16(Helper.vectorToByteArray(this.instructions), insPointer + 1);
                    insPointer += 2;
                    err = this.push(this.constants.get(constIndex));
                    if (err != null) {
                        return err;
                    }
                    break;
                case OpCodes.OpNull:
                    err = this.push(Constants.NULL);
                    if (err != null) {
                        return err;
                    }
                    break;

                case OpCodes.OpTrue:
                    err = this.push(Constants.TRUE);
                    if (err != null) {
                        return err;
                    }
                    break;

                case OpCodes.OpFalse:
                    err = this.push(Constants.FALSE);
                    if (err != null) {
                        return err;
                    }
                    break;

                case OpCodes.OpAdd:
                case OpCodes.OpSub:
                case OpCodes.OpMul:
                case OpCodes.OpDiv:
                case OpCodes.OpPow:
                    err = this.executeBinaryOperation(op);
                    if (err != null) {
                        return err;
                    }
                    break;

                case OpCodes.OpEqual:
                case OpCodes.OpNotEqual:
                case OpCodes.OpGreaterThan:
                case OpCodes.OpGreaterThanEqualTo:
                    err = this.executeComparision(op);
                    if (err != null) {
                        return err;
                    }
                    break;

                case OpCodes.OpBang:
                    err = this.executeBangOperator();
                    if (err != null) {
                        return err;
                    }
                    break;

                case OpCodes.OpMinus:
                    err = this.executeMinusOperation();
                    if (err != null) {
                        return err;
                    }
                    break;

                case OpCodes.OpPop:
                    this.pop();
                    break;
                case OpCodes.OpJump:
                    int pos = Code.readUint16(Helper.vectorToByteArray(this.instructions), insPointer + 1);
                    insPointer = pos - 1;
                    break;
                case OpCodes.OpJumpNotTruthy:
                    pos = Code.readUint16(Helper.vectorToByteArray(this.instructions), insPointer + 1);
                    insPointer += 2;

                    Object_T condition = this.pop();
                    if (!this.isTruth(condition)) {
                        insPointer = pos - 1;
                    }
                    break;
                case OpCodes.OpSetGlobal:
                    int globalIndex = Code.readUint16(Helper.vectorToByteArray(this.instructions), insPointer + 1);
                    insPointer += 2;
                    if (this.globals.size() <= globalIndex)
                        this.globals.add(globalIndex, this.pop());
                    else
                        this.globals.set(globalIndex, this.pop());
                    break;
                case OpCodes.OpGetGlobal:
                    globalIndex = Code.readUint16(Helper.vectorToByteArray(this.instructions), insPointer + 1);
                    insPointer += 2;
                    err = this.push(this.globals.get(globalIndex));
                    if (err != null) {
                        return err;
                    }
                    break;

                default:
                    return new VMError("", "Unsupported Operation : " + op);

            }
        }
        return err;
    }

    boolean isTruth(Object_T condition) {
        if (condition instanceof Boolean_T) {
            return ((Boolean_T) condition).getValue();
        } else {
            // NOTE: IF not boolean , should it be truthy ? If yes then why?
            return false;
        }
    }

    VMError executeMinusOperation() {
        Object_T operand = this.pop();
        if (operand instanceof Integer_T) {
            return this.push(new Integer_T(-((Integer_T) operand).getValue()));
        }
        return new VMError("", "Unsupported type : " + operand.type());
    }

    VMError executeBangOperator() {
        Object_T operand = this.pop();
        switch (operand) {
            case Boolean_T bool:
                return this.push(!bool.getValue() ? Constants.TRUE : Constants.FALSE);
            case Null_T obj:
                return this.push(Constants.TRUE);
            default:
                return this.push(Constants.FALSE);

        }
    }

    VMError executeBinaryOperation(byte op) {
        Object_T right = this.pop();
        Object_T left = this.pop();
        if (left instanceof Integer_T && right instanceof Integer_T) {
            return this.executeBinaryIntegerOperation(op, (Integer_T) left, (Integer_T) right);
        }
        return new VMError("", "Unsupported type : " + right.type() + " " + left.type());
    }

    VMError executeBinaryIntegerOperation(byte op, Integer_T left, Integer_T right) {
        int leftVal = left.getValue();
        int rightVal = right.getValue();
        switch (op) {
            case OpCodes.OpAdd:
                return this.push(new Integer_T(leftVal + rightVal));
            case OpCodes.OpSub:
                return this.push(new Integer_T(leftVal - rightVal));
            case OpCodes.OpMul:
                return this.push(new Integer_T(leftVal * rightVal));
            case OpCodes.OpDiv:
                return this.push(new Integer_T(leftVal / rightVal));
            case OpCodes.OpPow:
                return this.push(new Integer_T((int) Math.pow(leftVal, rightVal)));
            default:
                return new VMError("", "Unsupported Operation : " + op);
        }

    }

    VMError executeComparision(byte op) {
        Object_T right = this.pop();
        Object_T left = this.pop();
        if (left instanceof Integer_T && right instanceof Integer_T) {
            return this.executeIntegerComparision(op, (Integer_T) left, (Integer_T) right);
        }
        switch (op) {
            case OpCodes.OpEqual:
                return this.push(this.nativeBoolToBooleanObject(left == right));
            case OpCodes.OpNotEqual:
                return this.push(this.nativeBoolToBooleanObject(left != right));
            default:
                return new VMError("", "Unsupported operator : " + op);

        }
    }

    VMError executeIntegerComparision(byte op, Integer_T left, Integer_T right) {
        int leftVal = left.getValue();
        int rightVal = right.getValue();
        System.out.println("Left Value : " + leftVal);
        System.out.println("Right Value : " + rightVal);
        switch (op) {
            case OpCodes.OpEqual:
                return this.push(this.nativeBoolToBooleanObject(leftVal == rightVal));
            case OpCodes.OpNotEqual:
                return this.push(this.nativeBoolToBooleanObject(leftVal != rightVal));
            case OpCodes.OpGreaterThan:
                return this.push(this.nativeBoolToBooleanObject(leftVal > rightVal));
            case OpCodes.OpGreaterThanEqualTo:
                return this.push(this.nativeBoolToBooleanObject(leftVal >= rightVal));
            default:
                return new VMError("", "Unsupported operator : " + op);
        }

    }

    Boolean_T nativeBoolToBooleanObject(boolean input) {
        return input ? Constants.TRUE : Constants.FALSE;
    }

    public VMError push(Object_T o) {
        if (this.sp >= StackSize) {
            return new VMError("", "StackOverFlow");
        }
        this.stack.add(this.sp, o);
        this.sp++;
        return null;
    }

    public Object_T pop() {
        Object_T o = this.stack.get(this.sp - 1);
        this.sp--;
        return o;
    }

    public Object_T lastPoppedStackElement() {
        return this.stack.get(this.sp);
    }
}
