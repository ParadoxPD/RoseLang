package org.vm;

import org.typesystem.Compiled_Function_T;

import java.util.*;

/** Frame */
public class Frame {

    Compiled_Function_T fn;
    int insPointer;

    public Frame(Compiled_Function_T fn) {
        this.fn = fn;
        this.insPointer = -1;
    }

    public Vector<Byte> instructions() {
        return this.fn.getInstructions();
    }
}
