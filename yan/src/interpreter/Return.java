package interpreter;

public class Return extends RuntimeException{
    final YanObject value;

    public Return(YanObject value) {
        this.value = value;
    }
}
