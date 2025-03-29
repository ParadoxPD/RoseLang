package org.bytecode;

import java.util.HashMap;
import java.util.Map;

import org.bytecode.utils.Binary;
import org.bytecode.utils.Definition;
import org.bytecode.utils.OpCodes;

public class Code {
    Map<Byte, Definition> definitions = new HashMap<Byte, Definition>();

    public Code() {
        this.definitions.put(OpCodes.OpConstant, new Definition("OpConstant", new int[] { 2 }));
    }

    Definition lookUp(byte op) {
        Definition def = definitions.get(op);
        if (def == null) {
            System.out.println("OpCode " + op + " undefined");
        }

        return def;

    }

    byte[] make(byte op, int... operands) {
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

    Something readOperands(Definition def, byte[] ins, int offset1) {
        int[] operands = new int[def.operandWidth.length];
        int offset = 0;

        for (int i = 0; i < def.operandWidth.length; i++) {
            switch (def.operandWidth[i]) {
                case 2:
                    operands[i] = readUint16(ins, offset + offset1);
                    break;
            }
            offset += def.operandWidth[i];
        }
        return new Something(operands, offset);
    }

    int readUint16(byte[] ins, int offset) {
        return Binary.readUint16(ins, offset);
    }

    String toString(byte[] ins) {
        String res = "";
        for (int i = 0; i < ins.length; i++) {
            Definition def = this.lookUp(ins[i]);
            if (def != null) {
                Something some = readOperands(def, ins, i + 1);
                res += i + " " + fmtIns(ins, def, some.operands) + " ";
                i += some.offset + 1;
            } else {
                continue;
            }
        }
        return res;
    }

    static String fmtIns(byte[] ins, Definition def, int[] operands) {
        int operandCount = def.operandWidth.length;
        if (operands.length != operandCount) {
            return "ERROR: Operand Count does not match";
        }
        switch (operandCount) {
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
