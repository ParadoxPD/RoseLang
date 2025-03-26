
package org.compiler;

import org.parser.Node;
import org.typesystem.Object_T;

public class Compiler {

    byte[] instructions;
    Object_T[] constants;

    Compiler() {
        this.instructions = null;
        this.constants = null;

    }

    void compile(Node node) {

    }

    ByteCode bytecode() {
        return new ByteCode(this.instructions, this.constants);
    }

}

class ByteCode {
    byte[] instructions;
    Object_T[] constants;

    ByteCode(byte[] instructions, Object_T[] constants) {
        this.instructions = instructions;
        this.constants = constants;
    }
}
