package org.typesystem;

import org.typesystem.utils.*;

import java.util.Vector;

/** Compiled_Function_T */
public class Compiled_Function_T implements Object_T {

    Vector<Byte> instructions;
    int numLocals;
    int numParameters;

    public Compiled_Function_T(Vector<Byte> ins) {
        this.instructions = ins;
    }

    public Compiled_Function_T(Vector<Byte> ins, int numLocals) {
        this(ins);
        this.numLocals = numLocals;
    }

    public Compiled_Function_T(Vector<Byte> ins, int numLocals, int numParameters) {
        this(ins, numLocals);
        this.numParameters = numParameters;
    }

    @Override
    public String type() {
        return TypeList.COMPILED_FUNCTION_OBJECT;
    }

    @Override
    public String inspect() {
        return "Compiled Function : " + this;
    }

    public Vector<Byte> getInstructions() {
        return this.instructions;
    }

    public int getNumLocals() {
        return this.numLocals;
    }

    public int getNumParameters() {
        return this.numParameters;
    }
}
