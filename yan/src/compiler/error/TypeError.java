package compiler.error;

import error.CompilerError;

public class TypeError extends CompilerError {

    public TypeError(String description) {
        super("type error: " + description);
    }
}
