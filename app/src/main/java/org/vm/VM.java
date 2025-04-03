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
        if (operand instanceof Float_T) {
            return this.push(new Float_T(-((Float_T) operand).getValue()));
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
        if (left instanceof Primitive && right instanceof Primitive) {
            return this.executePrimitiveBinaryOperation(op, left, right);
        }
        return new VMError("", "Unsupported type : " + right.type() + " " + left.type());
    }

    VMError executePrimitiveBinaryOperation(byte op, Object_T left, Object_T right) {
        switch (left) {
            case Integer_T it when right instanceof Integer_T:
                return this.executeBinaryIntegerOperation(op, it, (Integer_T) right);
            case Integer_T it when right instanceof Boolean_T:
                return this.executeBinaryIntegerOperation(op, it,
                        new Integer_T(((Boolean_T) right).getValue() ? 1 : 0));
            case Boolean_T it when right instanceof Integer_T:
                return this.executeBinaryIntegerOperation(op, new Integer_T(it.getValue() ? 1 : 0), (Integer_T) right);

            case Float_T it when right instanceof Float_T:
                return this.executeBinaryFloatOperation(op, it, (Float_T) right);
            case Float_T it when right instanceof Integer_T:
                return this.executeBinaryFloatOperation(op, it, new Float_T(((Integer_T) right).getValue()));
            case Float_T it when right instanceof Boolean_T:
                return this.executeBinaryFloatOperation(op, it, new Float_T(((Boolean_T) right).getValue() ? 1 : 0));
            case Integer_T it when right instanceof Float_T:
                return this.executeBinaryFloatOperation(op, new Float_T(it.getValue()), (Float_T) right);
            case Boolean_T it when right instanceof Float_T:
                return this.executeBinaryFloatOperation(op, new Float_T(it.getValue() ? 1 : 0), (Float_T) right);

            case String_T it when right instanceof String_T:
                return this.executeBinaryStringOperation(op, it, (String_T) right);
            case String_T it when right instanceof Integer_T:
                return this.executeBinaryStringOperation(op, it, new String_T(((Integer_T) right).getValue() + ""));
            case String_T it when right instanceof Float_T:
                return this.executeBinaryStringOperation(op, it, new String_T(((Float_T) right).getValue() + ""));
            case String_T it when right instanceof Boolean_T:
                return this.executeBinaryStringOperation(op, it, new String_T(((Boolean_T) right).getValue() + ""));
            case Boolean_T it when right instanceof String_T:
                return this.executeBinaryStringOperation(op, new String_T(it.getValue() + ""), (String_T) right);
            case Float_T it when right instanceof String_T:
                return this.executeBinaryStringOperation(op, new String_T(it.getValue() + ""), (String_T) right);
            case Integer_T it when right instanceof String_T:
                return this.executeBinaryStringOperation(op, new String_T(it.getValue() + ""), (String_T) right);

            default:
                return new VMError("",
                        "Operation : " + op + " not permitted for type : " + left.type() + " " + right.type());
        }
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

    VMError executeBinaryFloatOperation(byte op, Float_T left, Float_T right) {
        float leftVal = left.getValue();
        float rightVal = right.getValue();
        switch (op) {
            case OpCodes.OpAdd:
                return this.push(new Float_T(leftVal + rightVal));
            case OpCodes.OpSub:
                return this.push(new Float_T(leftVal - rightVal));
            case OpCodes.OpMul:
                return this.push(new Float_T(leftVal * rightVal));
            case OpCodes.OpDiv:
                return this.push(new Float_T(leftVal / rightVal));
            case OpCodes.OpPow:
                return this.push(new Float_T((int) Math.pow(leftVal, rightVal)));
            default:
                return new VMError("", "Unsupported Operation : " + op);
        }

    }

    VMError executeBinaryStringOperation(byte op, String_T left, String_T right) {
        String leftVal = left.getValue();
        String rightVal = right.getValue();
        switch (op) {
            case OpCodes.OpAdd:
                return this.push(new String_T(leftVal + rightVal));

            default:
                return new VMError("", "Unsupported Operation : " + op);
        }

    }

    VMError executeComparision(byte op) {
        Object_T right = this.pop();
        Object_T left = this.pop();
        switch (left) {
            case Integer_T it when right instanceof Integer_T:
                return this.executeIntegerComparision(op, it, (Integer_T) right);

            case Float_T it when right instanceof Float_T:
                return this.executeFloatComparision(op, it, (Float_T) right);
            case Float_T it when right instanceof Integer_T:
                return this.executeFloatComparision(op, it, new Float_T(((Integer_T) right).getValue()));
            case Integer_T it when right instanceof Float_T:
                return this.executeFloatComparision(op, new Float_T(it.getValue()), (Float_T) right);

            case Boolean_T it when right instanceof Boolean_T:
                switch (op) {
                    case OpCodes.OpEqual:
                        return this.push(this.nativeBoolToBooleanObject(left == right));
                    case OpCodes.OpNotEqual:
                        return this.push(this.nativeBoolToBooleanObject(left != right));
                    default:
                        return new VMError("", "Unsupported operator : " + op);
                }
            default:
                return new VMError("", "Unsupported type : " + left.type() + " " + right.type());
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

    VMError executeFloatComparision(byte op, Float_T left, Float_T right) {
        float leftVal = left.getValue();
        float rightVal = right.getValue();
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
