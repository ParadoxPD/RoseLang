package org.compiler;

/**
 * Symbol
 */
public class Symbol {

    String name;
    String scope;
    int index;

    public Symbol(String name, String scope, int index) {
        this.name = name;
        this.scope = scope;
        this.index = index;
    }

}
