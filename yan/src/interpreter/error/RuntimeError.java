package interpreter.error;

import error.BaseError;
import frontend.Token;
import frontend.TokenType;

public class RuntimeError extends BaseError {
    public RuntimeError(Token token, String message) {
        super(message);
    }
}
