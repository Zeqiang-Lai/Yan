package error;

public class CompilerError extends Exception {
    public String description;
    public CompilerError(String description) {
        this.description = description;
    }
}