package org.compiler;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

/** SymbolTable */
public class SymbolTable {

    int numDefinitions;
    Map<String, Symbol> store;

    SymbolTable outer;

    Vector<Symbol> freeSymbols;

    public SymbolTable() {
        this.store = new HashMap<String, Symbol>();
        this.freeSymbols = new Vector<Symbol>();
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

    Symbol defineBuiltin(int index, String name) {
        Symbol symbol = new Symbol(name, Scopes.BuiltinScope, index);
        this.store.put(name, symbol);
        return symbol;
    }

    Symbol defineFree(Symbol original) {
        this.freeSymbols.add(original);
        Symbol symbol = new Symbol(original.name, Scopes.FreeScope, this.freeSymbols.size() - 1);
        this.store.put(original.name, symbol);
        return symbol;
    }

    Symbol resolve(String name) {
        Symbol symbol = this.store.get(name);
        if (symbol == null && this.outer != null) {
            symbol = this.outer.resolve(name);
            if (symbol != null) {
                if (symbol.scope == Scopes.BuiltinScope || symbol.scope == Scopes.GlobalScope) {
                    return symbol;
                }
                Symbol free = this.defineFree(symbol);
                return free;
            }
            return symbol;
        }
        return symbol;
    }

    Symbol resolveWithinScope(String name) {
        return this.store.get(name);
    }

    public void print() {
        System.out.println("Printing SYmbol table");
        for (String name : this.store.keySet()) {
            System.out.println(name + " : " + this.store.get(name));
        }
    }
}
