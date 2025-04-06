package org.vm;

import org.typesystem.Compiled_Function_T;

import java.util.*;

/** Frame */
public class Frame {

    Compiled_Function_T fn;
    int insPointer;
    int basePointer;

    public Frame(Compiled_Function_T fn, int basePointer) {
        this.fn = fn;
        this.insPointer = -1;
        this.basePointer = basePointer;
    }

    public Vector<Byte> instructions() {
        return this.fn.getInstructions();
    }
}
