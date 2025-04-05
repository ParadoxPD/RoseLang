package org.vm;

import org.code.*;
import org.code.utils.*;
import org.compiler.*;
import org.error.*;
import org.typesystem.*;
import org.typesystem.utils.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

/** VM */
public class VM {

    public static final int StackSize = 2048;
    public static final int GlobalsSize = 65536;
    public static final int MaxFrames = 1024;

    Vector<Object_T> constants;

    Vector<Object_T> stack;
    Vector<Object_T> globals;
    int sp;

    Vector<Frame> frames;
    int frameIndex;

    public VM(ByteCode bytecode) {

        this.frames = new Vector<Frame>(MaxFrames);
        this.frames.add(new Frame(new Compiled_Function_T(bytecode.getInstructions())));
        this.constants = bytecode.getConstants();
        this.stack = new Vector<Object_T>(StackSize);
        this.globals = new Vector<Object_T>(GlobalsSize);
        this.sp = 0;
        this.frameIndex = 1;
    }

    public VM(ByteCode bytecode, Vector<Object_T> globals) {
        this(bytecode);
        this.globals = globals;
    }

    public Frame currentFrame() {
        return this.frames.get(this.frameIndex - 1);
    }

    public void pushFrame(Frame f) {
        this.frames.add(f);
        this.frameIndex++;
    }

    public Frame popFrame() {
        this.frameIndex--;
        return this.frames.get(this.frameIndex);
    }

    public Object_T stackTop() {
        if (this.sp == 0) {
            return null;
        }

        return this.stack.get(this.sp - 1);
    }

    public VMError run() {
        int insPointer;
        Vector<Byte> ins;
        byte op;
        VMError err = null;

        while (this.currentFrame().insPointer < this.currentFrame().instructions().size() - 1) {
            this.currentFrame().insPointer++;

            insPointer = this.currentFrame().insPointer;
            ins = this.currentFrame().instructions();
            op = ins.get(insPointer);

            System.out.println("Operator : " + OpCodes.Definitions.get(op).name);
            switch (op) {
                case OpCodes.OpConstant:
                    int constIndex = Code.readUint16(Helper.vectorToByteArray(ins), insPointer + 1);
                    this.currentFrame().insPointer += 2;
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
                    int pos = Code.readUint16(Helper.vectorToByteArray(ins), insPointer + 1);
                    this.currentFrame().insPointer = pos - 1;
                    break;
                case OpCodes.OpJumpNotTruthy:
                    pos = Code.readUint16(Helper.vectorToByteArray(ins), insPointer + 1);
                    this.currentFrame().insPointer += 2;

                    Object_T condition = this.pop();
                    if (!this.isTruth(condition)) {
                        this.currentFrame().insPointer = pos - 1;
                    }
                    break;
                case OpCodes.OpSetGlobal:
                    int globalIndex =
                            Code.readUint16(Helper.vectorToByteArray(ins), insPointer + 1);
                    this.currentFrame().insPointer += 2;
                    if (this.globals.size() <= globalIndex)
                        this.globals.add(globalIndex, this.pop());
                    else this.globals.set(globalIndex, this.pop());
                    break;
                case OpCodes.OpGetGlobal:
                    globalIndex = Code.readUint16(Helper.vectorToByteArray(ins), insPointer + 1);
                    this.currentFrame().insPointer += 2;
                    err = this.push(this.globals.get(globalIndex));
                    if (err != null) {
                        return err;
                    }
                    break;

                case OpCodes.OpArray:
                    int numElements =
                            Code.readUint16(Helper.vectorToByteArray(ins), insPointer + 1);
                    this.currentFrame().insPointer += 2;
                    Object_T array = this.buildArray(this.sp - numElements, this.sp);
                    this.sp = this.sp - numElements;
                    err = this.push(array);
                    if (err != null) {
                        return err;
                    }
                    break;
                case OpCodes.OpHash:
                    numElements = Code.readUint16(Helper.vectorToByteArray(ins), insPointer + 1);
                    this.currentFrame().insPointer += 2;
                    Object_T hash = this.buildHash(this.sp - numElements, this.sp);
                    if (hash == null) {
                        return new VMError("", "Key is not hashable");
                    }
                    this.sp = this.sp - numElements;
                    err = this.push(hash);
                    if (err != null) {
                        return err;
                    }
                    break;
                case OpCodes.OpIndex:
                    Object_T index = this.pop();
                    Object_T left = this.pop();

                    err = this.executeIndexExpression(left, index);
                    if (err != null) {
                        return err;
                    }
                    break;
                case OpCodes.OpCall:
                    Object_T fn = this.stack.get(this.sp - 1);
                    if (fn instanceof Compiled_Function_T) {
                        Frame frame = new Frame((Compiled_Function_T) fn);
                        this.pushFrame(frame);
                    } else {
                        return new VMError("", "Calling non function");
                    }
                    break;
                case OpCodes.OpReturnValue:
                    Object_T returnValue = this.pop();
                    this.popFrame();
                    this.pop();
                    err = this.push(returnValue);
                    if (err != null) {
                        return err;
                    }
                    break;
                case OpCodes.OpReturn:
                    this.popFrame();
                    this.pop();

                    err = this.push(Constants.NULL);
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

    VMError executeIndexExpression(Object_T left, Object_T index) {
        if (left instanceof Array_T && index instanceof Integer_T) {
            return this.evalArrayIndexExpression((Array_T) left, (Integer_T) index);
        } else if (left instanceof Hash_T) {
            return this.evalHashIndexExpression((Hash_T) left, index);
        } else {
            return new VMError("", "Index Operator not supported : " + left.type());
        }
    }

    VMError evalArrayIndexExpression(Array_T left, Integer_T index) {
        int idx = index.getValue();
        int max = left.getValue().size() - 1;
        if (idx < 0 || idx > max) {
            this.push(Constants.NULL);
            return new VMError("", "Array index out of range : " + idx);
        }
        return this.push(left.getValue().get(idx));
    }

    VMError evalHashIndexExpression(Hash_T hash, Object_T index) {
        if (!(index instanceof Hashable)) {
            return new VMError("", "Unusable as hash key : " + index.type());
        }
        HashPair pair = hash.getPairs().get(((Hashable) index).hash());
        if (pair == null) {
            System.out.println("aaaaaaaaaaaaahhhhhhhh");
            this.push(Constants.NULL);
            return new VMError("", "Undefined Key : " + index.inspect());
        }
        return this.push(pair.getValue());
    }

    Array_T buildArray(int start, int end) {
        // Vector<Object_T> elems = new Vector<Object_T>();
        // for (int i = start; i < end; i++) {
        //  elems.add(this.stack.get(i));
        // }
        return new Array_T(Helper.slice(this.stack, start, end));
    }

    Hash_T buildHash(int start, int end) {
        Map<HashKey, HashPair> pairs =
                new HashMap<HashKey, HashPair>() {
                    @Override
                    public HashPair get(Object key) {
                        Set<HashKey> keys = this.keySet();
                        for (HashKey k : keys) {
                            if (k.getKey() == ((HashKey) key).getKey()) {
                                return super.get(k);
                            }
                        }
                        return null;
                    }
                };
        for (int i = start; i < end; i += 2) {
            Object_T key = this.stack.get(i);
            if (!(key instanceof Hashable)) {
                return null;
            }
            Object_T value = this.stack.get(i + 1);
            HashPair pair = new HashPair(key, value);

            pairs.put(((Hashable) key).hash(), pair);
        }
        return new Hash_T(pairs);
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
            case Integer_T it
            when right instanceof Integer_T:
                return this.executeBinaryIntegerOperation(op, it, (Integer_T) right);
            case Integer_T it
            when right instanceof Boolean_T:
                return this.executeBinaryIntegerOperation(
                        op, it, new Integer_T(((Boolean_T) right).getValue() ? 1 : 0));
            case Boolean_T it
            when right instanceof Integer_T:
                return this.executeBinaryIntegerOperation(
                        op, new Integer_T(it.getValue() ? 1 : 0), (Integer_T) right);

            case Float_T it
            when right instanceof Float_T:
                return this.executeBinaryFloatOperation(op, it, (Float_T) right);
            case Float_T it
            when right instanceof Integer_T:
                return this.executeBinaryFloatOperation(
                        op, it, new Float_T(((Integer_T) right).getValue()));
            case Float_T it
            when right instanceof Boolean_T:
                return this.executeBinaryFloatOperation(
                        op, it, new Float_T(((Boolean_T) right).getValue() ? 1 : 0));
            case Integer_T it
            when right instanceof Float_T:
                return this.executeBinaryFloatOperation(
                        op, new Float_T(it.getValue()), (Float_T) right);
            case Boolean_T it
            when right instanceof Float_T:
                return this.executeBinaryFloatOperation(
                        op, new Float_T(it.getValue() ? 1 : 0), (Float_T) right);

            case String_T it
            when right instanceof String_T:
                return this.executeBinaryStringOperation(op, it, (String_T) right);
            case String_T it
            when right instanceof Integer_T:
                return this.executeBinaryStringOperation(
                        op, it, new String_T(((Integer_T) right).getValue() + ""));
            case String_T it
            when right instanceof Float_T:
                return this.executeBinaryStringOperation(
                        op, it, new String_T(((Float_T) right).getValue() + ""));
            case String_T it
            when right instanceof Boolean_T:
                return this.executeBinaryStringOperation(
                        op, it, new String_T(((Boolean_T) right).getValue() + ""));
            case Boolean_T it
            when right instanceof String_T:
                return this.executeBinaryStringOperation(
                        op, new String_T(it.getValue() + ""), (String_T) right);
            case Float_T it
            when right instanceof String_T:
                return this.executeBinaryStringOperation(
                        op, new String_T(it.getValue() + ""), (String_T) right);
            case Integer_T it
            when right instanceof String_T:
                return this.executeBinaryStringOperation(
                        op, new String_T(it.getValue() + ""), (String_T) right);

            default:
                return new VMError(
                        "",
                        "Operation : "
                                + op
                                + " not permitted for type : "
                                + left.type()
                                + " "
                                + right.type());
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
            case Integer_T it
            when right instanceof Integer_T:
                return this.executeIntegerComparision(op, it, (Integer_T) right);

            case Float_T it
            when right instanceof Float_T:
                return this.executeFloatComparision(op, it, (Float_T) right);
            case Float_T it
            when right instanceof Integer_T:
                return this.executeFloatComparision(
                        op, it, new Float_T(((Integer_T) right).getValue()));
            case Integer_T it
            when right instanceof Float_T:
                return this.executeFloatComparision(
                        op, new Float_T(it.getValue()), (Float_T) right);

            case Boolean_T it
            when right instanceof Boolean_T:
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
