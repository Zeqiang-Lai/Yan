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
        scopes.push(global);
        this.current = this.global;
        this.types.push(Type.GLOBAL);
    }

    public void beginScope(Type type) {
        this.types.push(type);
        scopes.push(new HashMap<>());
        current = scopes.peek();
    }

    public void endScope() {
        scopes.pop();
        current = scopes.peek();
        this.types.pop();
    }

    public Type type() {
        return types.peek();
    }

    public Map<String, Symbol> getScope(int idx) {
        return scopes.get(idx);
    }
}
