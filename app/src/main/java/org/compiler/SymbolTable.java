package org.compiler;

import java.util.HashMap;
import java.util.Map;

/**
 * SymbolTable
 */
public class SymbolTable {

    int numDefinitions;
    Map<String, Symbol> store;

    public SymbolTable() {
        this.store = new HashMap<String, Symbol>();

    }

    Symbol define(String name) {
        Symbol symbol = new Symbol(name, Scopes.GlobalScope, this.numDefinitions);
        this.store.put(name, symbol);
        this.numDefinitions++;
        return symbol;
    }

    Symbol resolve(String name) {
        return this.store.get(name);
    }

}
