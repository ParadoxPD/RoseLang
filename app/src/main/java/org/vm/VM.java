package org.vm;

import java.util.Vector;

import org.code.*;
import org.code.utils.*;
import org.compiler.*;
import org.typesystem.*;
import org.typesystem.utils.*;

/**
 * VM
 */
public class VM {

    final static int StackSize = 2048;

    Vector<Object_T> constants;
    Vector<Byte> instructions;

    Vector<Object_T> stack;
    int sp;

    public VM(ByteCode bytecode) {
        this.instructions = bytecode.getInstructions();
        this.constants = bytecode.getConstants();

        this.stack = new Vector<Object_T>(StackSize);
        this.sp = 0;
    }

    public Object_T stackTop() {
        if (this.sp == 0) {
            return null;
        }

        return this.stack.get(this.sp - 1);
    }

    public void run() {
        for (int insPointer = 0; insPointer < this.instructions.size(); insPointer++) {
            byte op = this.instructions.get(insPointer);
            switch (op) {
                case OpCodes.OpConstant:
                    int constIndex = Code.readUint16(Helper.vectorToByteArray(this.instructions), insPointer + 1);
                    insPointer += 2;
                    if (this.push(this.constants.get(constIndex)) != 0) {
                        return;
                    }
                    break;
                case OpCodes.OpNull:
                    this.push(Constants.NULL);
                    break;
                case OpCodes.OpTrue:
                    this.push(Constants.TRUE);
                    break;
                case OpCodes.OpFalse:
                    this.push(Constants.FALSE);
                    break;
                case OpCodes.OpAdd:
                case OpCodes.OpSub:
                case OpCodes.OpMul:
                case OpCodes.OpDiv:
                case OpCodes.OpPow:
                    this.executeBinaryOperation(op);
                    break;
                case OpCodes.OpEqual:
                case OpCodes.OpNotEqual:
                case OpCodes.OpGreaterThan:
                case OpCodes.OpGreaterThanEqualTo:
                    this.executeComparision(op);
                    break;
                case OpCodes.OpBang:
                    this.executeBangOperator();
                    break;
                case OpCodes.OpMinus:
                    this.executeMinusOperation();
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
                default:
                    break;

            }
        }
    }

    boolean isTruth(Object_T condition) {
        if (condition instanceof Boolean_T) {
            return ((Boolean_T) condition).getValue();
        } else {
            // NOTE: IF not boolean , should it be truthy ? If yes then why?
            return false;
        }
    }

    void executeMinusOperation() {
        Object_T operand = this.pop();
        if (operand instanceof Integer_T) {
            this.push(new Integer_T(-((Integer_T) operand).getValue()));
        }
    }

    void executeBangOperator() {
        Object_T operand = this.pop();
        switch (operand) {
            case Boolean_T bool:
                this.push(!bool.getValue() ? Constants.TRUE : Constants.FALSE);
                break;
            case Null_T obj:
                this.push(Constants.TRUE);
                break;
            default:
                this.push(Constants.FALSE);
                break;

        }
    }

    void executeBinaryOperation(byte op) {
        Object_T right = this.pop();
        Object_T left = this.pop();
        if (left instanceof Integer_T && right instanceof Integer_T) {
            this.executeBinaryIntegerOperation(op, (Integer_T) left, (Integer_T) right);
        }
    }

    void executeBinaryIntegerOperation(byte op, Integer_T left, Integer_T right) {
        int leftVal = left.getValue();
        int rightVal = right.getValue();
        switch (op) {
            case OpCodes.OpAdd:
                this.push(new Integer_T(leftVal + rightVal));
                break;
            case OpCodes.OpSub:
                this.push(new Integer_T(leftVal - rightVal));
                break;
            case OpCodes.OpMul:
                this.push(new Integer_T(leftVal * rightVal));
                break;
            case OpCodes.OpDiv:
                this.push(new Integer_T(leftVal / rightVal));
                break;
            case OpCodes.OpPow:
                this.push(new Integer_T((int) Math.pow(leftVal, rightVal)));
                break;
            default:
                break;
        }

    }

    void executeComparision(byte op) {
        Object_T right = this.pop();
        Object_T left = this.pop();
        if (left instanceof Integer_T && right instanceof Integer_T) {
            this.executeIntegerComparision(op, (Integer_T) left, (Integer_T) right);
            return;
        }
        switch (op) {
            case OpCodes.OpEqual:
                this.push(this.nativeBoolToBooleanObject(left == right));
                break;
            case OpCodes.OpNotEqual:
                this.push(this.nativeBoolToBooleanObject(left != right));
                break;
            default:
                break;

        }
    }

    void executeIntegerComparision(byte op, Integer_T left, Integer_T right) {
        int leftVal = left.getValue();
        int rightVal = right.getValue();
        System.out.println("Left Value : " + leftVal);
        System.out.println("Right Value : " + rightVal);
        switch (op) {
            case OpCodes.OpEqual:
                this.push(this.nativeBoolToBooleanObject(leftVal == rightVal));
                break;
            case OpCodes.OpNotEqual:
                this.push(this.nativeBoolToBooleanObject(leftVal != rightVal));
                break;
            case OpCodes.OpGreaterThan:
                this.push(this.nativeBoolToBooleanObject(leftVal > rightVal));
                break;
            case OpCodes.OpGreaterThanEqualTo:
                this.push(this.nativeBoolToBooleanObject(leftVal >= rightVal));
                break;
            default:
                break;
        }

    }

    Boolean_T nativeBoolToBooleanObject(boolean input) {
        return input ? Constants.TRUE : Constants.FALSE;
    }

    public int push(Object_T o) {
        if (this.sp >= StackSize) {
            return 1;
        }
        this.stack.add(this.sp, o);
        this.sp++;
        return 0;
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
