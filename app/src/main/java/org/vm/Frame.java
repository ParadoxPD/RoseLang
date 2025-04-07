package org.vm;

import org.typesystem.Closure_T;

import java.util.*;

/** Frame */
public class Frame {

    int insPointer;
    int basePointer;
    Closure_T closure;

    public Frame(Closure_T closure, int basePointer) {
        this.insPointer = -1;
        this.basePointer = basePointer;
        this.closure = closure;
    }

    public Vector<Byte> instructions() {
        return this.closure.getFunction().getInstructions();
    }
}
