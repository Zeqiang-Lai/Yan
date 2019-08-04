package compiler;

import compiler.error.NameError;
import frontend.ast.StmtNode;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class Scope {
    enum Type {
        IF, ELSE, LOOP, FUNCTION, BLOCK, GLOBAL;
    }

    public Map<String, Symbol> symbols = new HashMap<>();

    public final StmtNode code;

    public final Type type;

    public Scope(StmtNode code, Type type) {
        this.code = code;
        this.type = type;
    }

    public Symbol get(String identifier) {
        if(!symbols.containsKey(identifier))
            return null;
        return symbols.get(identifier);
    }

    public void put(String identifier, Symbol symbol) {
        if(symbols.containsKey(identifier))
            throw new NameError(identifier, true);
        symbols.put(identifier, symbol);
    }
}
