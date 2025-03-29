package org.bytecode;

import org.typesystem.Object_T;

public class ByteCode {
    byte[] instructions;
    Object_T[] constants;

    public ByteCode(byte[] instructions, Object_T[] constants) {
        this.instructions = instructions;
        this.constants = constants;
    }
}
