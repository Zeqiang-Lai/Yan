package compiler;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class Scope {
    enum Type {
        IF, LOOP, FUNCTION, BLOCK, GLOBAL;
    }

    private Stack<Map<String, Symbol>> scopes = new Stack<>();
    private Stack<Type> types = new Stack<>();
    private final Map<String, Symbol> global = new HashMap<>();
    public Map<String, Symbol> current;


    public Scope() {
        this.scopes.push(global);
        this.current = this.global;
        this.types.push(Type.GLOBAL);
    }

    public void beginScope(Type type) {
        scopes.push(new HashMap<>());
        types.push(type);
        current = scopes.peek();
    }

    public void endScope() {
        scopes.pop();
        types.pop();
        current = scopes.peek();
    }

    public Symbol get(String identifier) {
        for(int i=scopes.size()-1; i>=0; i--) {
            if(scopes.get(i).containsKey(identifier))
                return scopes.get(i).get(identifier);
        }
        return null;
    }

    public Type type() {
        return types.peek();
    }

    public Map<String, Symbol> getScope(int idx) {
        return scopes.get(idx);
    }
}
