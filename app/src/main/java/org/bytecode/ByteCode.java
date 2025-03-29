package org.bytecode;

import java.util.Vector;

import org.typesystem.Object_T;

public class ByteCode {
    Vector<Byte> instructions;
    Vector<Object_T> constants;

    public ByteCode(Vector<Byte> instructions, Vector<Object_T> constants) {
        this.instructions = instructions;
        this.constants = constants;
    }
}
