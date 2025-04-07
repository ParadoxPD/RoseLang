package org.typesystem;

import org.typesystem.utils.*;

import java.util.Vector;

/** Compiled_Function_T */
public class Closure_T implements Object_T {

    Compiled_Function_T func;
    Vector<Object_T> free;

    public Closure_T(Compiled_Function_T func) {

        this.func = func;
        this.free = new Vector<Object_T>();
    }

    public Closure_T(Compiled_Function_T func, Vector<Object_T> free) {
        this.func = func;
        this.free = free;
    }

    @Override
    public String type() {
        return TypeList.CLOSURE_OBJECT;
    }

    @Override
    public String inspect() {
        return "Closure : " + this;
    }

    public Vector<Object_T> getFree() {
        return this.free;
    }

    public Compiled_Function_T getFunction() {
        return this.func;
    }
}
