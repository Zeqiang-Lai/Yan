package error;

public class CompilerError extends RuntimeException {
    public String description;
    public CompilerError(String description) {
        this.description = description;
    }
}