package org.code;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.code.utils.Binary;
import org.code.utils.Definition;
import org.code.utils.Helper;
import org.code.utils.OpCodes;

public class Code {
    Map<Byte, Definition> definitions = new HashMap<Byte, Definition>();

    public Code() {
        this.definitions.put(OpCodes.OpConstant, new Definition("OpConstant", new int[] { 2 }));

        this.definitions.put(OpCodes.OpTrue, new Definition("OpTrue", new int[] {}));
        this.definitions.put(OpCodes.OpFalse, new Definition("OpFalse", new int[] {}));

        this.definitions.put(OpCodes.OpAdd, new Definition("OpAdd", new int[] {}));
        this.definitions.put(OpCodes.OpSub, new Definition("OpSub", new int[] {}));
        this.definitions.put(OpCodes.OpMul, new Definition("OpMul", new int[] {}));
        this.definitions.put(OpCodes.OpDiv, new Definition("OpDiv", new int[] {}));
        this.definitions.put(OpCodes.OpPow, new Definition("OpPow", new int[] {}));

        this.definitions.put(OpCodes.OpEqual, new Definition("OpEqual", new int[] {}));
        this.definitions.put(OpCodes.OpNotEqual, new Definition("OpNotEqual", new int[] {}));
        this.definitions.put(OpCodes.OpGreaterThan, new Definition("OpGreaterThan", new int[] {}));
        this.definitions.put(OpCodes.OpGreaterThanEqualTo, new Definition("OpGreaterThanEqualTo", new int[] {}));

        this.definitions.put(OpCodes.OpPop, new Definition("OpPop", new int[] {}));
    }

    Definition lookUp(byte op) {
        Definition def = definitions.get(op);
        if (def == null) {
            System.out.println("OpCode " + op + " undefined");
        }

        return def;

    }

    public byte[] make(byte op, int... operands) {
        Definition def = definitions.get(op);
        if (def == null) {
            return new byte[] {};
        }
        int instructionLen = 1;
        for (int width : def.operandWidth) {
            instructionLen += width;
        }
        byte[] instruction = new byte[instructionLen];
        instruction[0] = op;

        int offset = 1;
        for (int i = 0; i < operands.length; i++) {
            int width = def.operandWidth[i];
            switch (width) {
                case 2:
                    // NOTE: Something to do with some big-endian shit that i dont understand for
                    // now(sadj)

                    // NOTE: Something i came up with, no idea if it works or not but have to test
                    // it latere
                    Binary.putUint16(instruction, (short) operands[i], offset);
                    break;
            }
            offset += width;
        }
        return instruction;
    }

    public static Something readOperands(Definition def, Vector<Byte> ins, int offset1) {
        int[] operands = new int[def.operandWidth.length];
        int offset = 0;

        for (int i = 0; i < def.operandWidth.length; i++) {
            switch (def.operandWidth[i]) {
                case 2:
                    operands[i] = readUint16(Helper.vectorToByteArray(ins), offset + offset1);
                    break;
            }
            offset += def.operandWidth[i];
        }
        return new Something(operands, offset);
    }

    public static int readUint16(byte[] ins, int offset) {
        return Binary.readUint16(ins, offset);
    }

    public String toString(Vector<Byte> ins) {
        String res = "";
        int i = 0;
        while (i < ins.size()) {
            Definition def = this.lookUp(ins.get(i));
            if (def != null) {
                Something some = readOperands(def, ins, i + 1);
                res += i + " " + fmtIns(ins, def, some.operands) + "\n";
                i += some.offset + 1;
            } else {
                continue;
            }
        }
        return res;
    }

    static String fmtIns(Vector<Byte> ins, Definition def, int[] operands) {
        int operandCount = def.operandWidth.length;
        if (operands.length != operandCount) {
            return "ERROR: Operand Count does not match";
        }
        switch (operandCount) {
            case 0:
                return def.name;
            case 1:
                return def.name + " " + operands[0];
        }
        return "ERROR No idea";
    }

}

class Something {
    int[] operands;
    int offset;

    Something(int[] operands, int offset) {
        this.operands = operands;
        this.offset = offset;
    }
}
