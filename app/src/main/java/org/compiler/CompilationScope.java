package org.compiler;

import java.util.Vector;

/** CompilationScope */
public class CompilationScope {

    Vector<Byte> instructions;
    EmittedInstruction lastInstruction;
    EmittedInstruction previousInstruction;

    public CompilationScope() {
        this.instructions = new Vector<Byte>();
        this.lastInstruction = new EmittedInstruction();
        this.previousInstruction = new EmittedInstruction();
    }
}
