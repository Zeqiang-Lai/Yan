package compiler.error;

import error.BaseError;

public class TypeError extends BaseError {

    public TypeError(String description) {
        super(" type error: " + description);
    }
}
