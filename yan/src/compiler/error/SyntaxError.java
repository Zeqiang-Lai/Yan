package compiler.error;

import error.CompilerError;

public class SyntaxError extends CompilerError {
    public SyntaxError(String description) {
        super(" syntax error: " + description);
    }
}
