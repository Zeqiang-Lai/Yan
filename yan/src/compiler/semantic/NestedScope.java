package compiler.semantic;

import compiler.error.NameError;
import frontend.ast.StmtNode;

import java.util.Stack;

public class NestedScope {
    private Stack<Scope> scopes = new Stack<>();
    private final Scope global = new Scope(null, Scope.Type.GLOBAL);

    public Scope current;

    public NestedScope() {
        this.scopes.push(global);
        this.current = this.global;
    }

    public void beginScope(StmtNode code, Scope.Type type) {
        scopes.push(new Scope(code, type));
        current = scopes.peek();
    }

    public void endScope() {
        scopes.pop();
        current = scopes.peek();
    }

    public Symbol get(String identifier) {
        Symbol symbol;
        for(int i=scopes.size()-1; i>=0; i--) {
            symbol = scopes.get(i).get(identifier);
            if(symbol != null)
                return symbol;
        }
        throw new NameError(identifier, false);
    }

    public Scope find(Scope.Type type) {
        for(int i=scopes.size()-1; i>=0; i--) {
            if(scopes.get(i).type == type)
                return scopes.get(i);
        }
        return null;
    }
}
