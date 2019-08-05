package compiler.error;

import error.BaseError;

public class SyntaxError extends BaseError {
    public SyntaxError(String description) {
        super(" syntax error: " + description);
    }
}
