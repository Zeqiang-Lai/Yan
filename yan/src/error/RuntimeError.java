package error;

import frontend.Token;

public class RuntimeError extends CompilerError{
    public RuntimeError(Token token, String message) {
        super(message);
    }
}
