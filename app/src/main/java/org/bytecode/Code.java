package org.bytecode;

import java.util.Map;
import java.util.HashMap;

interface OpCodes {
    byte OpConstant = 1;
}

public class Code {

    // Byte[] instrs
    // Byte opcode

    Map<Byte, Definition> definitions = new HashMap<Byte, Definition>();

    Definition lookUp(byte op) {
        Definition def = definitions.get(op);

        return def;

    }

    byte[] make(byte op, int[] operands) {
        Definition def = definitions.get(op);
        if (def == null) {
            return null;
        }
        int instructionLen = 1;
        for (int w : def.operandWidth) {
            instructionLen += w;
        }
        byte[] instruction = new byte[instructionLen];
        instruction[0] = op;

        int offset = 1;
        for (int i = 0; i < operands.length; i++) {
            int width = def.operandWidth[i];
            switch (width) {
                case 2:
                    // NOTE: Something to do with some big-endian shit that i dont understand for
                    // now
                    break;
            }
            offset += width;
        }
        return instruction;

    }

    Something readOperands(Definition def, byte[] ins){
        int[] operands = new int[def.operandWidth.length];
        int offset = 0;

        for(int i =0;i<def.operandWidth.length;i++){
            switch(def.operandWidth[i]){
                case 2:
                operands[i] = readUint(ins[offset:]);
                break;
            }
            offset += def.operandWidth[i];
        }
        return new Something(operands,offset);
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

class Definition {
    String name;
    int[] operandWidth;

}
