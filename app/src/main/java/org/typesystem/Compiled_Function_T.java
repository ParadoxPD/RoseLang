package org.typesystem;

import org.typesystem.utils.*;

import java.util.Vector;

/** Compiled_Function_T */
public class Compiled_Function_T implements Object_T {

    Vector<Byte> instructions;

    public Compiled_Function_T(Vector<Byte> ins) {
        this.instructions = ins;
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
}
