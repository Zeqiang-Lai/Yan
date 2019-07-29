package interpreter.error;

import error.CompilerError;
import frontend.Token;

public class RuntimeError extends CompilerError {
    public RuntimeError(Token token, String message) {
        super(message);
    }
}
