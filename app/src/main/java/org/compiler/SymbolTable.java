package org.compiler;

import java.util.HashMap;
import java.util.Map;

/** SymbolTable */
public class SymbolTable {

    int numDefinitions;
    Map<String, Symbol> store;

    SymbolTable outer;

    public SymbolTable() {
        this.store = new HashMap<String, Symbol>();
        this.outer = null;
    }

    public SymbolTable(SymbolTable outer) {
        this();
        this.outer = outer;
    }

    Symbol define(String name) {
        Symbol symbol = new Symbol(name, Scopes.GlobalScope, this.numDefinitions);

        symbol.scope = this.outer == null ? Scopes.GlobalScope : Scopes.LocalScope;

        this.store.put(name, symbol);
        this.numDefinitions++;
        return symbol;
    }

    Symbol resolve(String name) {
        Symbol symbol = this.store.get(name);
        if (symbol == null && this.outer != null) {
            symbol = this.outer.resolve(name);
            return symbol;
        }
        return symbol;
    }

    Symbol resolveWithinScope(String name) {
        return this.store.get(name);
    }

    Symbol defineBuiltin(int index, String name) {
        Symbol symbol = new Symbol(name, Scopes.BuiltinScope, index);
        this.store.put(name, symbol);
        return symbol;
    }

    public void print() {
        System.out.println("Printing SYmbol table");
        for (String name : this.store.keySet()) {
            System.out.println(name + " : " + this.store.get(name));
        }
    }
}
