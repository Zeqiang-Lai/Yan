package error;

public class BaseError extends RuntimeException {
    public String description;
    public BaseError(String description) {
        this.description = description;
    }
}