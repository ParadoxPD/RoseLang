package org.vm;

import java.util.Vector;

import org.code.*;
import org.code.utils.*;
import org.compiler.*;
import org.typesystem.*;

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
                case OpCodes.OpAdd:
                    Object_T right = this.pop();
                    Object_T left = this.pop();
                    int leftVal = ((Integer_T) left).getValue();
                    int rightVal = ((Integer_T) right).getValue();

                    int result = leftVal + rightVal;
                    this.push(new Integer_T(result));
                    break;

            }
        }
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
}
