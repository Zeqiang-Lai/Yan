package compiler.error;

import error.CompilerError;

public class NameError extends CompilerError {
    public NameError(String name, boolean is_defined) {
        super("");
        StringBuilder builder = new StringBuilder();
        builder.append("NameError: name '").append(name).append("'");
        if(is_defined)
            builder.append(" has already defined");
        else
            builder.append(" is not defined");
        this.description = builder.toString();
    }
}
